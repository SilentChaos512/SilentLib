package net.silentchaos512.lib.event;

import com.mojang.datafixers.types.Func;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Can be used to give players items when they first join a world. Call {@link #addItem(Identifier, Function)} or
 * {@link #addItems(Identifier, Function)} in either init or post-init. <em>This should be used
 * sparingly</em>; we spawn with enough junk already. It is recommended to have config options to
 * disable spawn items.
 *
 * @author SilentChaos512
 * @since 3.0.3
 */
@ParametersAreNonnullByDefault
public final class InitialSpawnItems {
    private static final InitialSpawnItems INSTANCE = new InitialSpawnItems();
    private static final String NBT_KEY = SilentLib.MOD_ID + ".SpawnItemsGiven";

    private final Map<Identifier, Function<Player, Collection<ItemStack>>> spawnItems = new HashMap<>();

    private InitialSpawnItems() {
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
    }

    /**
     * Add a spawn item. If the supplier returns an empty stack ({@link ItemStack#EMPTY}), nothing
     * is given. If the item is given, the player will never receive a spawn item with the same
     * {@code key} again. If no item is given, the key will not be marked as given.
     *
     * @param key         The key to uniquely identify the spawn item
     * @param itemFactory The item stack producer. Should not contain empty stacks.
     * @deprecated Use {@link #addItem(Identifier, Function)} or {@link #addItems(Identifier, Function)} instead
     */
    @Deprecated
    public static void add(Identifier key, Function<Player, Collection<ItemStack>> itemFactory) {
        addItems(key, itemFactory);
    }

    public static void addItem(Identifier key, Function<Player, ItemStack> itemSupplier) {
        INSTANCE.spawnItems.put(key, p -> Collections.singleton(itemSupplier.apply(p)));
    }

    public static void addItems(Identifier key, Function<Player, Collection<ItemStack>> itemsSupplier) {
        INSTANCE.spawnItems.put(key, itemsSupplier);
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CompoundTag givenItems = PlayerUtils.getPersistedDataSubcompound(player, NBT_KEY);

        spawnItems.forEach((key, factory) -> handleSpawnItems(player, givenItems, key, factory));
    }

    private static void handleSpawnItems(Player player, CompoundTag givenItems, Identifier key, Function<Player, Collection<ItemStack>> itemSupplier) {
        String nbtKey = key.toString().replace(':', '.');
        var hasBeenGiven = givenItems.getBoolean(nbtKey);
        if (hasBeenGiven.isEmpty() || !hasBeenGiven.get()) {
            itemSupplier.apply(player).forEach(stack -> {
                SilentLib.LOGGER.debug("Giving player {} spawn item \"{}\": {}", player.getScoreboardName(), nbtKey, stack);
                PlayerUtils.giveItem(player, stack);
                givenItems.putBoolean(nbtKey, true);
            });
        }
    }
}
