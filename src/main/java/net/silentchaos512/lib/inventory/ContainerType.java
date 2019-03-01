package net.silentchaos512.lib.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Borrowed from CC-Tweaked with minor modifications
 * <p>
 * Original: https://github.com/SquidDev-CC/CC-Tweaked/blob/mc-1.13.x/src/main/java/dan200/computercraft/shared/network/container/ContainerType.java
 *
 * @param <T> Container type
 */
public interface ContainerType<T extends Container> extends IInteractionObject {
    ResourceLocation getId();

    void toBytes(PacketBuffer buf);

    void fromBytes(PacketBuffer buf);

    @Override
    @SuppressWarnings("unchecked")
    default T createContainer(InventoryPlayer inventoryPlayer, EntityPlayer entityPlayer) {
        return ((BiFunction<ContainerType<T>, EntityPlayer, T>) containerFactories.get(getId())).apply(this, entityPlayer);
    }

    @Override
    default String getGuiID() {
        return getId().toString();
    }

    @Override
    default ITextComponent getName() {
        ResourceLocation id = getId();
        return new TextComponentTranslation("container." + id.getNamespace() + "." + id.getPath());
    }

    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    default ITextComponent getCustomName() {
        return null;
    }

    default void open(EntityPlayer player) {
        NetworkHooks.openGui((EntityPlayerMP) player, this, this::toBytes);
    }

    static <C extends Container, T extends ContainerType<C>> void register(Supplier<T> containerType, BiFunction<T, EntityPlayer, C> factory) {
        factories.put(containerType.get().getId(), containerType);
        containerFactories.put(containerType.get().getId(), factory);
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui(Supplier<T> containerType, BiFunction<T, EntityPlayer, GuiContainer> factory) {
        guiFactories.put(containerType.get().getId(), factory);
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui(Supplier<T> containerType, Function<C, GuiContainer> factory) {
        registerGui(containerType, (type, player) -> {
            @SuppressWarnings("unchecked")
            C container = ((BiFunction<T, EntityPlayer, C>) containerFactories.get(type.getId())).apply(type, player);
            return container == null ? null : factory.apply(container);
        });
    }

    Map<ResourceLocation, Supplier<? extends ContainerType<?>>> factories = new ConcurrentHashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, EntityPlayer, GuiContainer>> guiFactories = new ConcurrentHashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, EntityPlayer, ? extends Container>> containerFactories = new ConcurrentHashMap<>();
}
