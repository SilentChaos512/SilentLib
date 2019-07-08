package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;

import java.util.Collection;

public final class LootUtils {
    private LootUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static Collection<ItemStack> gift(ResourceLocation lootTable, ServerPlayerEntity player) {
        MinecraftServer server = player.world.getServer();
        if (server == null) return ImmutableList.of();

        LootContext lootContext = (new LootContext.Builder(player.getServerWorld()))
                .withParameter(LootParameters.THIS_ENTITY, player)
                .withParameter(LootParameters.POSITION, player.getPosition())
                .withLuck(player.getLuck())
                .build(LootParameterSets.GIFT);
        return server.getLootTableManager().getLootTableFromLocation(lootTable).generate(lootContext);
    }
}
