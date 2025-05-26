package net.silentchaos512.lib.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;

/**
 * Helper methods for working with entity attributes.
 *
 * @author SilentChaos512
 * @since 2.3.2
 */
public final class AttributeHelper {
    private AttributeHelper() {throw new IllegalAccessError("Utility class");}

    public static void apply(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttribute(Holder.direct(attribute));
        apply(instance, modifier);
    }

    public static void apply(@Nullable AttributeInstance attributeInstance, AttributeModifier modifier) {
        if (attributeInstance == null) return;
        AttributeModifier currentMod = attributeInstance.getModifier(modifier.id());

        if (currentMod != null && (!MathUtils.doublesEqual(currentMod.amount(), modifier.amount()) || currentMod.operation() != modifier.operation())) {
            // Modifier changed, so it needs to be reapplied
            attributeInstance.removeModifier(currentMod.id());
        } else {
            attributeInstance.addPermanentModifier(modifier);
        }
    }

    public static void remove(LivingEntity entity, Attribute attribute, ResourceLocation id) {
        AttributeInstance instance = entity.getAttribute(Holder.direct(attribute));
        remove(instance, id);
    }

    public static void remove(@Nullable AttributeInstance attributeInstance, ResourceLocation id) {
        if (attributeInstance == null) return;
        attributeInstance.removeModifier(id);
    }
}
