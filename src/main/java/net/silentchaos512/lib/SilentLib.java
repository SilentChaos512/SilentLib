package net.silentchaos512.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.lib.command.internal.TeleportCommand;
import net.silentchaos512.lib.component.LootContainer;
import net.silentchaos512.lib.item.ILeftClickItem;
import net.silentchaos512.lib.world.placement.DimensionFilterPlacement;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(SilentLib.MOD_ID)
public final class SilentLib {
    public static final String MOD_ID = "silentlib";
    public static final Logger LOGGER = LogUtils.getLogger();

    static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES_REGISTRAR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE_REGISTRAR =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LootContainer>> LOOT_CONTAINER = DATA_COMPONENT_TYPES_REGISTRAR.register(
            "loot_container",
            () -> DataComponentType.<LootContainer>builder()
                    .persistent(LootContainer.CODEC)
                    .networkSynchronized(LootContainer.STREAM_CODEC)
                    .build()
    );

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DimensionFilterPlacement>> DIMENSION_FILTER_PLACEMENT = PLACEMENT_MODIFIER_TYPE_REGISTRAR.register(
            "dimension_filter",
            () -> () -> DimensionFilterPlacement.CODEC
    );

    public SilentLib(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES_REGISTRAR.register(modEventBus);
        PLACEMENT_MODIFIER_TYPE_REGISTRAR.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        ILeftClickItem.EventHandler.init();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
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
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
