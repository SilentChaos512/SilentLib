package net.silentchaos512.lib.util;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootTableManager;
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
     * @param world World reference (needed to get {@link LootTableManager})
     * @return A list of missing loot tables, or an empty collection if there are none
     * @since 4.5.0
     */
    public static Collection<ResourceLocation> getMissingLootTables(String modId, ServerWorld world) {
        LootTableManager lootTableManager = world.getServer().getLootTableManager();
        Collection<ResourceLocation> missing = new ArrayList<>();

        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            ResourceLocation lootTable = block.getLootTable();
            // The AirBlock check filters out removed blocks
            if (lootTable.getNamespace().equals(modId) && !(block instanceof AirBlock) && !lootTableManager.getLootTableKeys().contains(lootTable)) {
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
     * @param world  World reference (needed to get {@link LootTableManager})
     * @param logger If not null, separate errors will be logged for missing loot tables. Does not
     *               affect the return value.
     * @return An {@link ITextComponent} if there are missing block loot tables, or null if none are
     * missing.
     * @since 4.5.0
     */
    @Nullable
    public static ITextComponent checkAndReportMissingLootTables(String modId, ServerWorld world, @Nullable Logger logger) {
        LootTableManager lootTableManager = world.getServer().getLootTableManager();
        Collection<ResourceLocation> missing = getMissingLootTables(modId, world);

        if (!missing.isEmpty()) {
            if (logger != null) {
                missing.forEach(id -> logger.error("Missing block loot table '{}'", id));
            }
            String list = missing.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            return new StringTextComponent("The following block loot tables are missing: " + list).applyTextStyle(TextFormatting.RED);
        }

        return null;
    }
}
