package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.silentchaos512.lib.command.internal.DisplayNBTCommand;
import net.silentchaos512.lib.command.internal.TeleportCommand;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(SilentLib.MOD_ID)
public final class SilentLib {
    public static final String MOD_ID = "silentlib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    private static SilentLib INSTANCE;

    public SilentLib(IEventBus modEventBus) {
        INSTANCE = this;

        modEventBus.addListener(this::registerPayloadHandler);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        ILeftClickItem.EventHandler.init();
    }

    private void registerPayloadHandler(RegisterPayloadHandlerEvent event) {
        SilentLibNetwork.register(event.registrar(MOD_ID));
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        DisplayNBTCommand.register(dispatcher);
        TeleportCommand.register(dispatcher);
    }

    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "0.0.0";
    }

    public static boolean isDevBuild() {
        return "NONE".equals(getVersion());
    }

    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
