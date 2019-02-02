package net.silentchaos512.lib;

import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.util.generator.TagGenerator;

class SideProxy {
    SideProxy() {
        FMLModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLModLoadingContext.get().getModEventBus().addListener(this::imcEnqueue);
        FMLModLoadingContext.get().getModEventBus().addListener(this::imcProcess);

        LibTriggers.init();
        ILeftClickItem.EventHandler.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) { }

    private void imcEnqueue(InterModEnqueueEvent event) { }

    private void imcProcess(InterModProcessEvent event) {
        //noinspection deprecation -- deprecated as a warning to modders, see javadoc
        TagGenerator.generateFiles();
    }

    static class Client extends SideProxy {
        Client() {
            FMLModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) { }
    }

    static class Server extends SideProxy {
        Server() {
            FMLModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) { }
    }
}
