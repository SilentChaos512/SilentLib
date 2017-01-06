package net.silentchaos512.lib.proxy;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.silentchaos512.lib.registry.SRegistry;

public class CommonProxy {

  public CommonProxy() {

  }

  public void preInit(SRegistry registry) {

    registry.preInit();
  }

  public void init(SRegistry registry) {

    registry.init();
  }

  public void postInit(SRegistry registry) {

    registry.postInit();
  }
}
