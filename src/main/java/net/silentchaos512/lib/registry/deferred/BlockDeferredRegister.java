package net.silentchaos512.lib.registry.deferred;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.silentchaos512.lib.registry.BlockRegistryObject;

import java.util.function.Supplier;

public class BlockDeferredRegister extends DeferredRegisterWrapper<Block> {
    public BlockDeferredRegister(String modid, IForgeRegistry<Block> registry) {
        super(modid, registry);
    }

    /**
     * Register all the registered blocks to item via {@link net.minecraftforge.registries.DeferredRegister<Item>}
     *
     * @see ItemDeferredRegister
     */
    public void registerBlockItems(ItemDeferredRegister itemDeferredRegister) {
        super.getEntries().forEach(block -> itemDeferredRegister.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(itemDeferredRegister.defaultTab))));
    }

    /**
     * Register all the registered blocks to item via {@link RegisterEvent}.
     *
     * @see <a href="https://docs.minecraftforge.net/en/1.19.x/concepts/registries/#registerevent">Register Event</a>
     */
    public void registerBlockItems(RegisterEvent.RegisterHelper<Item> registerHelper, CreativeModeTab defaultTab) {
        super.getEntries().forEach(block -> registerHelper.register(block.getId(), new BlockItem(block.get(), new Item.Properties().tab(defaultTab))));
    }

    public <T extends Block> BlockRegistryObject<T> register(String name, Supplier<T> blockSupplier) {
        return new BlockRegistryObject<>(this.deferredRegister, name, blockSupplier);
    }

    public BlockRegistryObject<Block> register(String name, Material material, int strength) {
        return register(name, material, strength, strength);
    }

    public BlockRegistryObject<Block> register(String name, Material material, int hardness, int resistance) {
        return register(name, () -> new Block(BlockBehaviour.Properties.of(material).strength(hardness, resistance)));
    }

    public BlockRegistryObject<Block> registerCopy(String name, Supplier<? extends Block> otherBlock) {
        return register(name, () -> new Block(BlockBehaviour.Properties.copy(otherBlock.get())));
    }
}
