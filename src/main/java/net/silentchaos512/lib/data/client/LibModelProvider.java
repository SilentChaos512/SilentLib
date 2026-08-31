package net.silentchaos512.lib.data.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// Adapted from GlitchCore (https://github.com/Glitchfiend/GlitchCore/blob/1.21.5/common/src/main/java/glitchcore/data/ModelProviderBase.java)
public abstract class LibModelProvider implements DataProvider {
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final String modId;
    private final boolean autogenBlockItemInfos;

    public LibModelProvider(PackOutput output, String modId, boolean autogenBlockItemInfos) {
        this.blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.modId = modId;
        this.autogenBlockItemInfos = autogenBlockItemInfos;
    }

    public LibModelProvider(PackOutput output, String modId) {
        this(output, modId, true);
    }

    @Nullable
    abstract protected BlockModelGenerators createBlockModelGenerators(Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput);

    @Nullable
    abstract protected ItemModelGenerators createItemModelGenerators(ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        ItemInfoCollector itemInfoCollector = new ItemInfoCollector();
        BlockStateGeneratorCollector blockStateGeneratorCollector = new BlockStateGeneratorCollector();
        SimpleModelCollector simpleModelCollector = new SimpleModelCollector();

        var blockModelGenerators = createBlockModelGenerators(blockStateGeneratorCollector, itemInfoCollector, simpleModelCollector);
        var itemModelGenerators = createItemModelGenerators(itemInfoCollector, simpleModelCollector);

        if (blockModelGenerators != null) blockModelGenerators.run();
        if (itemModelGenerators != null) itemModelGenerators.run();

        itemInfoCollector.finalizeAndValidate();
        return CompletableFuture.allOf(
                blockStateGeneratorCollector.save(output, this.blockStatePathProvider),
                simpleModelCollector.save(output, this.modelPathProvider),
                itemInfoCollector.save(output, this.itemInfoPathProvider)
        );
    }

    static <T> CompletableFuture<?> saveAll(CachedOutput output, Function<T, Path> p_386455_, Map<T, ? extends Supplier<JsonElement>> generators) {
        return DataProvider.saveAll(output, Supplier::get, p_386455_, generators);
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    static class BlockStateGeneratorCollector implements Consumer<BlockModelDefinitionGenerator> {
        private final Map<Block, BlockModelDefinitionGenerator> generators = new HashMap<>();

        public void accept(BlockModelDefinitionGenerator p_388748_) {
            Block block = p_388748_.block();
            BlockModelDefinitionGenerator blockstategenerator = this.generators.put(block, p_388748_);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        }

        public CompletableFuture<?> save(CachedOutput p_388014_, PackOutput.PathProvider p_388192_) {
            Map<Block, BlockStateModelDispatcher> map = Maps.transformValues(this.generators, BlockModelDefinitionGenerator::create);
            Function<Block, Path> function = block -> p_388192_.json(BuiltInRegistries.BLOCK.getKey(block));
            return DataProvider.saveAll(p_388014_, BlockStateModelDispatcher.CODEC, function, map);
        }
    }

    class ItemInfoCollector implements ItemModelOutput {
        private final Map<Item, ClientItem> itemInfos = new HashMap<>();
        private final Map<Item, Item> copies = new HashMap<>();

        @Override
        public void accept(Item item, ItemModel.Unbaked model) {
            this.register(item, new ClientItem(model, ClientItem.Properties.DEFAULT));
        }

        @Override
        public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
            this.register(item, new ClientItem(model, properties));
        }

        public void register(Item p_388205_, ClientItem p_388233_) {
            ClientItem clientitem = this.itemInfos.put(p_388205_, p_388233_);
            if (clientitem != null) {
                throw new IllegalStateException("Duplicate item model definition for " + p_388205_);
            }
        }

        @Override
        public void copy(Item p_386920_, Item p_386789_) {
            this.copies.put(p_386789_, p_386920_);
        }

        public void finalizeAndValidate() {
            if (LibModelProvider.this.autogenBlockItemInfos) {
                BuiltInRegistries.ITEM.listElements().filter(LibModelProvider.this::isModded).forEach(p_388426_ -> {
                    if (!this.copies.containsKey(p_388426_)) {
                        if (p_388426_.value() instanceof BlockItem blockitem && !this.itemInfos.containsKey(blockitem)) {
                            Identifier resourcelocation = ModelLocationUtils.getModelLocation(blockitem.getBlock());
                            this.accept(blockitem, ItemModelUtils.plainModel(resourcelocation));
                        }
                    }
                });
            }
            this.copies.forEach((p_386494_, p_386575_) -> {
                ClientItem clientitem = this.itemInfos.get(p_386575_);
                if (clientitem == null) {
                    throw new IllegalStateException("Missing donor: " + p_386575_ + " -> " + p_386494_);
                } else {
                    this.register(p_386494_, clientitem);
                }
            });
        }

        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            return DataProvider.saveAll(
                    output, ClientItem.CODEC, item -> pathProvider.json(BuiltInRegistries.ITEM.getKey(item)), this.itemInfos
            );
        }
    }

    static class SimpleModelCollector implements BiConsumer<Identifier, ModelInstance> {
        private final Map<Identifier, ModelInstance> models = new HashMap<>();

        public void accept(Identifier location, ModelInstance instance) {
            Supplier<JsonElement> supplier = this.models.put(location, instance);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + location);
            }
        }

        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            return saveAll(output, pathProvider::json, this.models);
        }
    }

    private boolean isModded(Holder.Reference<?> reference) {
        return reference.key().identifier().getNamespace().equals(this.modId);
    }
}
