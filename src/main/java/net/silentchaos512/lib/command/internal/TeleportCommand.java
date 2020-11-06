package net.silentchaos512.lib.command.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.silentchaos512.lib.util.DimensionId;
import net.silentchaos512.lib.util.TeleportUtils;

public final class TeleportCommand {
    private TeleportCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sl_tp")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("entity", EntityArgument.entities())
                        .then(Commands.argument("dimension", DimensionArgument.getDimension())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(TeleportCommand::run)
                                )
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        BlockPos target = BlockPosArgument.getBlockPos(context, "pos");
        ServerWorld world = DimensionArgument.getDimensionArgument(context, "dimension");

        for (Entity entity : EntityArgument.getEntities(context, "entity")) {
            if (entity instanceof PlayerEntity)
                TeleportUtils.teleport((PlayerEntity) entity, DimensionId.fromWorld(world), target.getX(), target.getY(), target.getZ(), null);
            TeleportUtils.teleportEntity(entity, world, target.getX(), target.getY(), target.getZ(), null);
        }

        return 1;
    }
}
