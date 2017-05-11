package net.silentchaos512.lib.util;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class EntityHelper {

  public static void moveSelf(Entity entity, double x, double y, double z) {

    entity.moveEntity(x, y, z);
  }

  public static List<String> getEntityNameList() {

    return EntityList.getEntityNameList();
  }
}
