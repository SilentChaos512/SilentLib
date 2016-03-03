package net.silentchaos512.lib.proxy;

import net.silentchaos512.lib.registry.SRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(SRegistry registry) {

    super.preInit(registry);
    registry.clientPreInit();
  }

  @Override
  public void init(SRegistry registry) {

    super.init(registry);
    registry.clientInit();
  }

  @Override
  public void postInit(SRegistry registry) {

    super.postInit(registry);
    registry.clientPostInit();
  }
}
