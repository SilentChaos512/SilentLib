package net.silentchaos512.lib.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Data component used by {@link net.silentchaos512.lib.item.LootContainerItem}
 *
 * @param lootTable The loot table to use
 * @since 9.2.0
 */
public record LootContainer(ResourceKey<LootTable> lootTable) {
    public static final Codec<LootContainer> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(lc -> lc.lootTable)
            ).apply(instance, LootContainer::new)
    );

    public static final StreamCodec<ByteBuf, LootContainer> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE), lc -> lc.lootTable,
            LootContainer::new
    );
}
