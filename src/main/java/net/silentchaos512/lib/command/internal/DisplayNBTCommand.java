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
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.silentchaos512.lib.network.internal.DisplayNBTPacket;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;

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
        Component title = new TranslatableComponent(world.getBlockState(pos).getBlock().getDescriptionId());

        if (tileEntity != null) {
            sendPacket(context, tileEntity.save(new CompoundTag()), title);
            return 1;
        }

        context.getSource().sendFailure(new TranslatableComponent("command.silentlib.nbt.notBlockEntity", title));
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
            context.getSource().sendFailure(new TranslatableComponent("command.silentlib.nbt.noItemInHand"));
            return 0;
        } else if (!stack.hasTag()) {
            context.getSource().sendFailure(new TranslatableComponent("command.silentlib.nbt.noItemTag", stack.getHoverName()));
            return 0;
        }

        sendPacket(context, stack.getOrCreateTag(), stack.getHoverName());
        return 1;
    }

    private static void sendPacket(CommandContext<CommandSourceStack> context, CompoundTag nbt, Component title) throws CommandSyntaxException {
        DisplayNBTPacket msg = new DisplayNBTPacket(nbt, textOfNullable(title));
        Connection netManager = context.getSource().getPlayerOrException().connection.connection;
        SilentLibNetwork.channel.sendTo(msg, netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static Component textOfNullable(@Nullable Component text) {
        // Just in case a mod does something stupid
        return text == null ? new TextComponent("null") : text;
    }
}
