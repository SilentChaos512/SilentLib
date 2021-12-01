package net.silentchaos512.lib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class BlockUtils {
    private BlockUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Gets a list of missing block loot tables. This is useful in development environments, but
     * should not be called in release builds, as missing loot tables are a development error.
     *
     * @param modId The mod ID
     * @param world World reference (needed to get {@link LootTables})
     * @return A list of missing loot tables, or an empty collection if there are none
     * @since 4.5.0
     */
    public static Collection<ResourceLocation> getMissingLootTables(String modId, ServerLevel world) {
        LootTables lootTableManager = world.getServer().getLootTables();
        Collection<ResourceLocation> missing = new ArrayList<>();

        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            ResourceLocation lootTable = block.getLootTable();
            // The AirBlock check filters out removed blocks
            if (lootTable.getNamespace().equals(modId) && !(block instanceof AirBlock) && !lootTableManager.getIds().contains(lootTable)) {
                missing.add(lootTable);
            }
        }

        return missing;
    }

    /**
     * Gets a message to display in chat for missing block loot tables. This is intended to be used
     * with {@link net.silentchaos512.lib.event.Greetings}. Missing loot table warnings should only
     * be displayed in development builds.
     *
     * @param modId  The mod ID
     * @param world  World reference (needed to get {@link LootTables})
     * @param logger If not null, separate errors will be logged for missing loot tables. Does not
     *               affect the return value.
     * @return An {@link Component} if there are missing block loot tables, or null if none are
     * missing.
     * @since 4.5.0
     */
    @Nullable
    public static Component checkAndReportMissingLootTables(String modId, ServerLevel world, @Nullable Logger logger) {
        LootTables lootTableManager = world.getServer().getLootTables();
        Collection<ResourceLocation> missing = getMissingLootTables(modId, world);

        if (!missing.isEmpty()) {
            if (logger != null) {
                missing.forEach(id -> logger.error("Missing block loot table '{}'", id));
            }
            String list = missing.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return new TextComponent("The following block loot tables are missing: " + list).withStyle(ChatFormatting.RED);
        }

        return null;
    }
}
