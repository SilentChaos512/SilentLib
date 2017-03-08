package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;

public class EntityHelper {

  public static void moveSelf(Entity entity, double x, double y, double z) {

    entity.moveEntity(x, y, z);
  }
}
