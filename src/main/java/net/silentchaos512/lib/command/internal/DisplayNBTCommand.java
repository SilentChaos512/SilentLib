package net.silentchaos512.lib.command.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.lib.network.internal.DisplayNBTPacket;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;

import javax.annotation.Nullable;

public class DisplayNBTCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sl_nbt")
                .requires(source -> source.hasPermissionLevel(2))
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

    private static int runForBlock(CommandContext<CommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        ServerWorld world = context.getSource().getWorld();
        TileEntity tileEntity = world.getTileEntity(pos);
        ITextComponent title = new TranslationTextComponent(world.getBlockState(pos).getBlock().getTranslationKey());

        if (tileEntity != null) {
            sendPacket(context, tileEntity.write(new CompoundNBT()), title);
            return 1;
        }

        context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentlib.nbt.notBlockEntity", title));
        return 0;
    }

    private static int runForEntity(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "target");
        CompoundNBT nbt = entity.writeWithoutTypeId(new CompoundNBT());
        ITextComponent title = entity.getDisplayName();
        sendPacket(context, nbt, title);
        return 1;
    }

    private static int runForItem(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().asPlayer().getHeldItemMainhand();
        if (stack.isEmpty()) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentlib.nbt.noItemInHand"));
            return 0;
        } else if (!stack.hasTag()) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("command.silentlib.nbt.noItemTag", stack.getDisplayName()));
            return 0;
        }

        sendPacket(context, stack.getOrCreateTag(), stack.getDisplayName());
        return 1;
    }

    private static void sendPacket(CommandContext<CommandSource> context, CompoundNBT nbt, ITextComponent title) throws CommandSyntaxException {
        DisplayNBTPacket msg = new DisplayNBTPacket(nbt, textOfNullable(title));
        NetworkManager netManager = context.getSource().asPlayer().connection.netManager;
        SilentLibNetwork.channel.sendTo(msg, netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static ITextComponent textOfNullable(@Nullable ITextComponent text) {
        // Just in case a mod does something stupid
        return text == null ? new StringTextComponent("null") : text;
    }
}
