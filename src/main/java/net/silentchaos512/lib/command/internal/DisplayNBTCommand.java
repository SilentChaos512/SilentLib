package net.silentchaos512.lib.command.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.lib.network.internal.SPacketDisplayNbt;

import javax.annotation.Nullable;

public class DisplayNBTCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sl_nbt")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("block")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(
                                        DisplayNBTCommand::runForBlock
                                )
                        )
                )
                .then(Commands.literal("entity")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(
                                        DisplayNBTCommand::runForEntity
                                )
                        )
                )
                .then(Commands.literal("item")
                        .executes(
                                DisplayNBTCommand::runForItem
                        )
                )
        );
    }

    private static int runForBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ServerLevel world = context.getSource().getLevel();
        BlockEntity tileEntity = world.getBlockEntity(pos);
        Component title = Component.translatable(world.getBlockState(pos).getBlock().getDescriptionId());

        if (tileEntity != null) {
            sendPacket(context, tileEntity.saveWithFullMetadata(), title);
            return 1;
        }

        context.getSource().sendFailure(Component.translatable("command.silentlib.nbt.notBlockEntity", title));
        return 0;
    }

    private static int runForEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "target");
        CompoundTag nbt = entity.saveWithoutId(new CompoundTag());
        Component title = entity.getDisplayName();
        sendPacket(context, nbt, title);
        return 1;
    }

    private static int runForItem(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().getPlayerOrException().getMainHandItem();
        if (stack.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.silentlib.nbt.noItemInHand"));
            return 0;
        } else if (!stack.hasTag()) {
            context.getSource().sendFailure(Component.translatable("command.silentlib.nbt.noItemTag", stack.getHoverName()));
            return 0;
        }

        sendPacket(context, stack.getOrCreateTag(), stack.getHoverName());
        return 1;
    }

    private static void sendPacket(CommandContext<CommandSourceStack> context, CompoundTag nbt, Component title) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
        SPacketDisplayNbt msg = new SPacketDisplayNbt(nbt, textOfNullable(title));
        PacketDistributor.PLAYER.with(serverPlayer).send(msg);
    }

    private static Component textOfNullable(@Nullable Component text) {
        // Just in case a mod does something stupid
        return text == null ? Component.literal("null") : text;
    }
}
