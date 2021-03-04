package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.TagRegistryManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.command.internal.DisplayNBTCommand;
import net.silentchaos512.lib.command.internal.TeleportCommand;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;
import net.silentchaos512.lib.data.recipe.test.TestRecipeProvider;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;

public class SideProxy {
    SideProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcProcess);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        SilentLibNetwork.init();
        LibTriggers.init();
        ILeftClickItem.EventHandler.init();
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(new TestRecipeProvider(gen));
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    private void imcEnqueue(InterModEnqueueEvent event) {}

    private void imcProcess(InterModProcessEvent event) {}

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        DisplayNBTCommand.register(dispatcher);
        TeleportCommand.register(dispatcher);
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(DamageItemRecipe.SERIALIZER.setRegistryName(SilentLib.getId("damage_item")));
    }

    public void tryFetchTagsHack() {}

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) {}

        @Override
        public void tryFetchTagsHack() {
            TagRegistryManager.fetchTags();
        }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {}
    }
}
