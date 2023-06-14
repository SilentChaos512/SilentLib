package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

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
        return new ItemEntity(dropper.level(), x, y, z, stack);
    }

    public static Collection<ItemStack> gift(ResourceLocation lootTable, ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return ImmutableList.of();

        LootParams lootParams = (new LootParams.Builder(player.serverLevel()))
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withLuck(player.getLuck())
                .create(LootContextParamSets.GIFT);
        return server.getLootData().getLootTable(lootTable).getRandomItems(lootParams);
    }
}
