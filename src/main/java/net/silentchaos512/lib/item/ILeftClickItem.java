package net.silentchaos512.lib.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.lib.network.internal.SwingItemPayload;

public interface ILeftClickItem {
    enum Target {
        /**
         * The player clicked and swung at the air (no target)
         */
        EMPTY,
        /**
         * The player clicked and swung at a nearby block
         */
        BLOCK;

        public static final StreamCodec<ByteBuf, Target> ID_STREAM_CODEC = ByteBufCodecs.idMapper(
                ByIdMap.continuous(
                        Target::ordinal,
                        Target.values(),
                        ByIdMap.OutOfBoundsStrategy.ZERO
                ),
                Target::ordinal
        );
    }

    /**
     * Networked left-click handler. Called in SilentLibEventHandlers on both the client- and
     * server-side (via packet) when a player left-clicks on nothing (in the air).
     *
     * @param world  The world
     * @param player The player
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default InteractionResultHolder<ItemStack> onItemLeftClickSL(Level world, Player player) {
        return new InteractionResultHolder<>(InteractionResult.PASS, player.getMainHandItem());
    }

    /**
     * Called when the player left-clicks on a block. Defaults to the same behavior as an empty
     * click (onItemLeftClickSL).
     *
     * @param world  The world
     * @param player The player
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default InteractionResultHolder<ItemStack> onItemLeftClickBlockSL(Level world, Player player) {
        return onItemLeftClickSL(world, player);
    }

    final class EventHandler {
        private EventHandler() {
        }

        public static void init() {
            NeoForge.EVENT_BUS.addListener(EventHandler::onLeftClickBlock);
            NeoForge.EVENT_BUS.addListener(EventHandler::onLeftClickEmpty);
        }

        private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem leftClickItem) {
                // Client-side call
                InteractionResultHolder<ItemStack> result = leftClickItem.onItemLeftClickBlockSL(event.getLevel(), event.getEntity());
                // Server-side call
                if (result.getResult() == InteractionResult.SUCCESS) {
                    PacketDistributor.sendToServer(new SwingItemPayload(Target.BLOCK));
                }
            }
        }

        private static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem leftClickItem) {
                // Client-side call
                InteractionResultHolder<ItemStack> result = leftClickItem.onItemLeftClickSL(event.getLevel(), event.getEntity());
                // Server-side call
                if (result.getResult() == InteractionResult.SUCCESS) {
                    PacketDistributor.sendToServer(new SwingItemPayload(Target.EMPTY));
                }
            }
        }
    }
}
