package net.silentchaos512.lib.inventory;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
 * FIXME!
 *
 * @param <T> Container type
 */
public interface ContainerType<T extends Container> extends INamedContainerProvider /*extends IInteractionObject*/ {
    ResourceLocation getId();

    void toBytes(PacketBuffer buf);

    void fromBytes(PacketBuffer buf);

//    @Override
    @SuppressWarnings("unchecked")
    default T createContainer(PlayerInventory inventoryPlayer, PlayerEntity entityPlayer) {
        return ((BiFunction<ContainerType<T>, PlayerEntity, T>) containerFactories.get(getId())).apply(this, entityPlayer);
    }

//    @Override
    default String getGuiID() {
        return getId().toString();
    }

    @Override
    default ITextComponent getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    default Container createMenu(int p_createMenu_1_, PlayerInventory playerInventory, PlayerEntity player) {
        return createContainer(playerInventory, player);
    }

    //    @Override
    default ITextComponent getName() {
        ResourceLocation id = getId();
        return new TranslationTextComponent("container." + id.getNamespace() + "." + id.getPath());
    }

//    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Nullable
//    @Override
    default ITextComponent getCustomName() {
        return null;
    }

    default void open(PlayerEntity player) {
        NetworkHooks.openGui((ServerPlayerEntity) player, this, this::toBytes);
    }

    static <C extends Container, T extends ContainerType<C>> void register(Supplier<T> containerType, BiFunction<T, PlayerEntity, C> factory) {
        factories.put(containerType.get().getId(), containerType);
        containerFactories.put(containerType.get().getId(), factory);
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui(Supplier<T> containerType, BiFunction<T, PlayerEntity, ContainerScreen<?>> factory) {
        guiFactories.put(containerType.get().getId(), factory);
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui(Supplier<T> containerType, Function<C, ContainerScreen<?>> factory) {
        registerGui(containerType, (type, player) -> {
            @SuppressWarnings("unchecked")
            C container = ((BiFunction<T, PlayerEntity, C>) containerFactories.get(type.getId())).apply(type, player);
            return container == null ? null : factory.apply(container);
        });
    }

    Map<ResourceLocation, Supplier<? extends ContainerType<?>>> factories = new ConcurrentHashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, PlayerEntity, ContainerScreen<?>>> guiFactories = new ConcurrentHashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, PlayerEntity, ? extends Container>> containerFactories = new ConcurrentHashMap<>();
}
