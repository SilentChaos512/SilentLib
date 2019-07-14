package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.command.internal.DisplayNBTCommand;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;
import net.silentchaos512.lib.util.generator.TagGenerator;

class SideProxy {
    SideProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcProcess);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        SilentLibNetwork.init();
        LibTriggers.init();
        ILeftClickItem.EventHandler.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) { }

    private void imcEnqueue(InterModEnqueueEvent event) { }

    private void imcProcess(InterModProcessEvent event) {
        //noinspection deprecation -- deprecated as a warning to modders, see javadoc
        TagGenerator.generateFiles();
    }

    private void onServerStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        DisplayNBTCommand.register(dispatcher);
    }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) { }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) { }
    }
}
