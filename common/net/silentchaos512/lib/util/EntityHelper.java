package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MoverType;

public class EntityHelper {

  public static void moveSelf(Entity entity, double x, double y, double z) {

    entity.move(MoverType.SELF, x, y, z);
  }

  /**
   * Simulates the 1.10.2 behavior of EntityList#getEntityNameList.
   * @since 2.1.3
   */
  public static List<String> getEntityNameList() {

    List<String> list = Lists.newArrayList();
    EntityList.getEntityNameList().forEach(res -> list.add(EntityList.getTranslationName(res)));
    return list;
  }
}
