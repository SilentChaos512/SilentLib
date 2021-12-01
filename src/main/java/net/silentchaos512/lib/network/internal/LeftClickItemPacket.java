package net.silentchaos512.lib.network.internal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.utils.EnumUtils;

import java.util.function.Supplier;

public class LeftClickItemPacket {
    private ILeftClickItem.ClickType clickType;
    private InteractionHand hand;

    public LeftClickItemPacket() {
        this.clickType = ILeftClickItem.ClickType.EMPTY;
        this.hand = InteractionHand.MAIN_HAND;
    }

    public LeftClickItemPacket(ILeftClickItem.ClickType clickType, InteractionHand hand) {
        this.clickType = clickType;
        this.hand = hand;
    }

    public ILeftClickItem.ClickType getClickType() {
        return clickType;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public static LeftClickItemPacket fromBytes(FriendlyByteBuf buffer) {
        LeftClickItemPacket packet = new LeftClickItemPacket();
        packet.clickType = EnumUtils.byOrdinal(buffer.readByte(), ILeftClickItem.ClickType.EMPTY);
        packet.hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return packet;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeByte(this.clickType.ordinal());
        buffer.writeBoolean(this.hand == InteractionHand.MAIN_HAND);
    }

    public static void handle(LeftClickItemPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();

        if (player != null) {
            ItemStack heldItem = player.getItemInHand(packet.hand);

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ILeftClickItem) {
                ILeftClickItem item = (ILeftClickItem) heldItem.getItem();

                if (packet.clickType == ILeftClickItem.ClickType.EMPTY) {
                    item.onItemLeftClickSL(player.level, player, packet.hand);
                } else if (packet.clickType == ILeftClickItem.ClickType.BLOCK) {
                    item.onItemLeftClickBlockSL(player.level, player, packet.hand);
                } else {
                    SilentLib.LOGGER.error("Unknown ILeftClickItem.ClickType: {}", packet.clickType);
                }
            }
        }

        context.get().setPacketHandled(true);
    }
}
