package net.silentchaos512.lib.collection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class EntityMatchList {

  private List<String> list = new ArrayList<>();
  private boolean whitelist = false;

  public boolean matches(Entity entity) {

    boolean contains = this.contains(entity);
    return contains == this.whitelist;
  }

  private boolean contains(Entity entity) {

    ResourceLocation resource = EntityList.getKey(entity);
    if (resource == null)
      return false;

    String id = resource.toString();
    String idOld = EntityList.getEntityString(entity);

    for (String entry : list)
      if (entry.equalsIgnoreCase(id) || entry.equalsIgnoreCase(idOld) || entry.equalsIgnoreCase("minecraft:" + id))
        return true;
    return false;
  }

  public void loadConfig(Configuration config, String name, String category, String[] defaults, boolean defaultWhitelist, boolean allowUserToChangeType, String comment) {

    String nameList = name + " List";
    String nameWhitelist = name + " IsWhitelist";

    this.list.clear();
    for (String str : config.getStringList(nameList, category, defaults, comment))
      list.add(str);

    if (allowUserToChangeType)
      this.whitelist = config.getBoolean(nameWhitelist, category, defaultWhitelist,
          "If true, the list is a whitelist. Otherwise it is a blacklist.");
    else
      this.whitelist = defaultWhitelist;
  }
}
