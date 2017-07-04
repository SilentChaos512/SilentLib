package net.silentchaos512.lib.util;

import java.util.List;
import java.util.Queue;

import com.google.common.collect.Queues;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class EntityHelper {

  public static void moveSelf(Entity entity, double x, double y, double z) {

    entity.move(x, y, z);
  }

  public static List<String> getEntityNameList() {

    return EntityList.getEntityNameList();
  }

  private static Queue<Entity> entitiesToSpawn = Queues.newArrayDeque();

  /**
   * Thread-safe spawning of entities. The queued entities will be spawned in the START (pre) phase of WorldTickEvent.
   * 
   * @since 2.1.4
   */
  public static void safeSpawn(Entity entity) {

    entitiesToSpawn.add(entity);
  }

  /**
   * Called by SilentLibCommonEvents#onWorldTick. Calling this yourself is not recommended.
   * 
   * @since 2.1.4
   */
  public static void handleSpawns() {

    Entity entity;
    while ((entity = entitiesToSpawn.poll()) != null) {
      entity.world.spawnEntity(entity);
    }
  }
}
