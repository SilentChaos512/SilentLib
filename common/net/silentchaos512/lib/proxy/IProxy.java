package net.silentchaos512.lib.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.registry.SRegistry;

public interface IProxy {

  void preInit(SRegistry registry, FMLPreInitializationEvent event);

  void init(SRegistry registry, FMLInitializationEvent event);

  void postInit(SRegistry registry, FMLPostInitializationEvent event);

  EntityPlayer getClientPlayer();

  int getParticleSettings();
}
