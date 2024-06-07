package net.silentchaos512.lib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentWithLevel(Enchantment enchantment, int level) {
    public static final Codec<EnchantmentWithLevel> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.ENCHANTMENT.byNameCodec().fieldOf("enchantment").forGetter(ed -> ed.enchantment),
                    Codec.INT.fieldOf("level").forGetter(ed -> ed.level)
            ).apply(instance, EnchantmentWithLevel::new)
    );

    public static EnchantmentWithLevel fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation enchantmentId = buf.readResourceLocation();
        var enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentId);
        if (enchantment == null) {
            throw new NullPointerException("Unknown enchantment: " + enchantmentId);
        }
        var level = buf.readByte();
        return new EnchantmentWithLevel(enchantment, level);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        var enchantmentId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (enchantmentId == null) {
            throw new IllegalStateException("Enchantment has no ID? " + enchantment);
        }
        buf.writeResourceLocation(enchantmentId);
        buf.writeByte(level);
    }
}
