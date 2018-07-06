/*
 * SilentLib - ModifierHelper
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import java.util.UUID;

/**
 * Helper methods for working with entity attributes.
 * TODO: Rename to AttributeHelper?
 *
 * @author SilentChaos512
 * @since 2.3.2
 */
public class ModifierHelper {

  public static void apply(EntityLivingBase entity, IAttribute attribute, UUID uuid, String name, double amount, int op) {

    IAttributeInstance instance = entity.getAttributeMap().getAttributeInstance(attribute);
    apply(instance, uuid, name, amount, op);
  }

  public static void apply(IAttributeInstance attributeInstance, UUID uuid, String name, double amount, int op) {

    if (attributeInstance == null)
      return;

    AttributeModifier currentMod = attributeInstance.getModifier(uuid);
    AttributeModifier newMod = new AttributeModifier(uuid, name, amount, op);

    if (currentMod != null && (currentMod.getAmount() != amount || currentMod.getOperation() != op)) {
      // Modifier has changed, so it needs to be reapplied
      attributeInstance.removeModifier(currentMod);
      attributeInstance.applyModifier(newMod);
    } else if (currentMod == null) {
      // Modifier has not been applied yet
      attributeInstance.applyModifier(newMod);
    }
  }

  public static void remove(EntityLivingBase entity, IAttribute attribute, UUID uuid) {

    IAttributeInstance instance = entity.getAttributeMap().getAttributeInstance(attribute);
    remove(instance, uuid);
  }

  public static void remove(IAttributeInstance attributeInstance, UUID uuid) {

    if (attributeInstance == null)
      return;

    attributeInstance.removeModifier(uuid);
  }
}
