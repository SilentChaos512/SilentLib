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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.silentchaos512.lib.util.TeleporterSL;

public class TeleportCommand {
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
        TeleporterSL teleporter = TeleporterSL.of(world, target);

        for (Entity entity : EntityArgument.getEntities(context, "entity")) {
            teleporter.teleport(entity);
        }

        return 1;
    }
}
