package net.silentchaos512.lib.registry;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * A singleton that has preInit, init, and postInit methods. If registered in the SRegistry, these are called
 * automatically.
 *
 * @author SilentChaos512
 * @since 2.3.2
 */
public interface IPhasedInitializer {

    default void preInit(SRegistry registry, FMLPreInitializationEvent event) {
    }

    default void init(SRegistry registry, FMLInitializationEvent event) {
    }

    default void postInit(SRegistry registry, FMLPostInitializationEvent event) {
    }
}
