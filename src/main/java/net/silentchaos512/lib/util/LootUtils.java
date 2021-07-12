package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public final class LootUtils {
    private LootUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Create an {@link ItemEntity} at the given {@code Entity}'s position. Does not add the new
     * entity to the world.
     *
     * @param stack   The item to drop
     * @param dropper The entity dropping the item
     * @return A new {@link ItemEntity} of the stack
     */
    public static ItemEntity createDroppedItem(ItemStack stack, Entity dropper) {
        double x = dropper.getX();
        double y = dropper.getY(dropper.getBbHeight() / 2);
        double z = dropper.getZ();
        return new ItemEntity(dropper.level, x, y, z, stack);
    }

    public static Collection<ItemStack> gift(ResourceLocation lootTable, ServerPlayerEntity player) {
        MinecraftServer server = player.level.getServer();
        if (server == null) return ImmutableList.of();

        LootContext lootContext = (new LootContext.Builder(player.getLevel()))
                .withParameter(LootParameters.THIS_ENTITY, player)
                .withParameter(LootParameters.ORIGIN, player.position())
                .withLuck(player.getLuck())
                .create(LootParameterSets.GIFT);
        return server.getLootTables().get(lootTable).getRandomItems(lootContext);
    }
}
