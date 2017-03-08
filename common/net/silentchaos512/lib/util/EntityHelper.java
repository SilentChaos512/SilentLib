package net.silentchaos512.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;

public class EntityHelper {

  public static void moveSelf(Entity entity, double x, double y, double z) {

    entity.move(MoverType.SELF, x, y, z);
  }
}
