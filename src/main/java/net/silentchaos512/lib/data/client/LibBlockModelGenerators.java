package net.silentchaos512.lib.data.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Extension features some additional methods for generating models, including cutout versions of some common plant
 * model types.
 *
 * @since 11.1.0 (MC 1.21.11)
 */
public class LibBlockModelGenerators extends BlockModelGenerators {
    public LibBlockModelGenerators(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
        super(blockStateOutput, itemModelOutput, modelOutput);
    }

    public void createCropCutoutBlock(Block cropBlock, Property<Integer> ageProperty, int... ageToVisualStageMapping) {
        this.registerSimpleFlatItemModel(cropBlock.asItem());
        if (ageProperty.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException();
        } else {
            Int2ObjectMap<Identifier> int2objectmap = new Int2ObjectOpenHashMap<>();
            this.blockStateOutput
                    .accept(
                            MultiVariantGenerator.dispatch(cropBlock)
                                    .with(
                                            PropertyDispatch.initial(ageProperty)
                                                    .generate(
                                                            p_465386_ -> {
                                                                int i = ageToVisualStageMapping[p_465386_];
                                                                return plainVariant(
                                                                        int2objectmap.computeIfAbsent(
                                                                                i,
                                                                                p_465395_ -> this.createSuffixedVariant(
                                                                                        cropBlock, "_stage" + p_465395_, LibModelTemplates.CROP_CUTOUT, TextureMapping::crop
                                                                                )
                                                                        )
                                                                );
                                                            }
                                                    )
                                    )
                    );
        }
    }

    public void createPlantCutoutWithDefaultItem(Block block, Block pottedBlock) {
        this.registerSimpleItemModel(block.asItem(), this.createFlatItemModelWithBlockTexture(block.asItem(), block));
        this.createPlantCutout(block, pottedBlock);
    }

    public void createPlantCutout(Block block, Block pottedBlock) {
        this.createCrossCutoutBlock(block);
        TextureMapping texturemapping = TextureMapping.plant(block);
        MultiVariant multivariant = plainVariant(LibModelTemplates.FLOWER_POT_CROSS_CUTOUT.create(pottedBlock, texturemapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(pottedBlock, multivariant));
    }

    public void createCrossCutoutBlock(Block block) {
        TextureMapping texturemapping = TextureMapping.cross(block);
        this.createCrossCutoutBlock(block, texturemapping);
    }

    public void createCrossCutoutBlock(Block block, TextureMapping textureMapping) {
        MultiVariant multivariant = plainVariant(LibModelTemplates.CROSS_CUTOUT.create(block, textureMapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(block, multivariant));
    }
}
