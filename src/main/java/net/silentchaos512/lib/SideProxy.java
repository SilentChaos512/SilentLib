package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.command.internal.DisplayNBTCommand;
import net.silentchaos512.lib.command.internal.TeleportCommand;
import net.silentchaos512.lib.crafting.ingredient.ExclusionIngredient;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRecipeSerializers);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        SilentLibNetwork.init();
        LibTriggers.init();
        ILeftClickItem.EventHandler.init();
        registerIngredientSerializers();
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(true, new TestRecipeProvider(gen));
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    private void imcEnqueue(InterModEnqueueEvent event) {}

    private void imcProcess(InterModProcessEvent event) {}

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        DisplayNBTCommand.register(dispatcher);
        TeleportCommand.register(dispatcher);
    }

    private void registerRecipeSerializers(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey())) return;

        event.getForgeRegistry().register(SilentLib.getId("damage_item"), DamageItemRecipe.SERIALIZER);
    }

    private void registerIngredientSerializers() {
        CraftingHelper.register(ExclusionIngredient.Serializer.NAME, ExclusionIngredient.Serializer.INSTANCE);
    }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) {}
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {}
    }
}
