package net.silentchaos512.lib.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;

/**
 * Data component used by {@link net.silentchaos512.lib.item.LootContainerItem}
 *
 * @param lootTable The loot table to use
 * @since 9.2.0
 */
public record LootContainer(ResourceKey<LootTable> lootTable) implements TooltipProvider {
    public static final Codec<LootContainer> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(lc -> lc.lootTable)
            ).apply(instance, LootContainer::new)
    );

    public static final StreamCodec<ByteBuf, LootContainer> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE), lc -> lc.lootTable,
            LootContainer::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag flag, DataComponentGetter componentGetter) {
        if (flag.isAdvanced()) {
            Component textTableName = Component.literal(this.lootTable.location().toString()).withStyle(ChatFormatting.WHITE);
            tooltipAdder.accept(Component.translatable("item.silentlib.lootContainer.table", textTableName).withStyle(ChatFormatting.BLUE));
        }
    }
}
