package net.silentchaos512.lib.command.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.lib.util.DimensionId;
import net.silentchaos512.lib.util.TeleportUtils;

public final class TeleportCommand {
    private TeleportCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sl_tp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entity", EntityArgument.entities())
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(TeleportCommand::run)
                                )
                        )
                )
        );
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos target = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ServerLevel world = DimensionArgument.getDimension(context, "dimension");

        for (Entity entity : EntityArgument.getEntities(context, "entity")) {
            if (entity instanceof Player)
                TeleportUtils.teleport((Player) entity, DimensionId.fromWorld(world), target.getX(), target.getY(), target.getZ(), null);
            TeleportUtils.teleportEntity(entity, world, target.getX(), target.getY(), target.getZ(), null);
        }

        return 1;
    }
}
