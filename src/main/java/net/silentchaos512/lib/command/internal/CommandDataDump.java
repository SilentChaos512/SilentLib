/*
 * Silent Lib -- CommandDataDump
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.command.internal;

import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.debug.DataDump;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CommandDataDump extends CommandBase {
    enum SubCommand implements IStringSerializable {
        ENCHANTMENT(DataDump::dumpEnchantments),
        ENTITY(DataDump::dumpEntityList),
        POTION(DataDump::dumpPotionEffects);

        private final Runnable command;

        SubCommand(Runnable command) {
            this.command = command;
        }

        void execute() {
            this.command.run();
        }

        @Override
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public String getName() {
        return SilentLib.MOD_ID + "_datadump";
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("slib_dump");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        StringBuilder subcommands = new StringBuilder();
        for (SubCommand sub : SubCommand.values()) {
            if (subcommands.length() > 0) subcommands.append("|");
            subcommands.append(sub.getName());
        }
        return String.format("%sUsage: /%s <%s>", TextFormatting.RED, getName(), subcommands.toString());
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        getSubCommand(args[0]).ifPresent(subCommand -> {
            subCommand.execute();
            String line = String.format("Printed %s list to log", subCommand.getName());
            sender.sendMessage(new TextComponentString(line));
        });
    }

    private Optional<SubCommand> getSubCommand(String arg) {
        for (SubCommand subCommand : SubCommand.values())
            if (subCommand.name().equalsIgnoreCase(arg))
                return Optional.of(subCommand);
        return Optional.empty();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, Arrays.stream(SubCommand.values())
                    .map(SubCommand::getName)
                    .collect(ImmutableList.toImmutableList()));
        else
            return ImmutableList.of();
    }
}
