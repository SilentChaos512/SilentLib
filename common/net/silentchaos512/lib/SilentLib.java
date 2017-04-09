package net.silentchaos512.lib;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.silentchaos512.lib.debug.DataDump;
import net.silentchaos512.lib.gui.GuiHandlerLibF;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;
import net.silentchaos512.lib.util.PlayerHelper;

@Mod(modid = SilentLib.MOD_ID, name = SilentLib.MOD_NAME, version = "SL_VERSION", dependencies = SilentLib.DEPENDENCIES)
public class SilentLib {

  public static final String MOD_ID = "silentlib";
  public static final String MOD_NAME = "Silent Lib";
  public static final String DEPENDENCIES = "required-after:forge@[13.19.0.2156,);";

  public static LogHelper logHelper = new LogHelper(MOD_NAME);

  private static final String NBT_ROOT_GUIDE_BOOKS = "silentlib_guide_books";

  @Instance(MOD_ID)
  public static SilentLib instance;

  private final Map<String, LocalizationHelper> locHelpers = Maps.newHashMap();

  public LocalizationHelper getLocalizationHelperForMod(String modId) {

    return locHelpers.get(modId.toLowerCase());
  }

  public void registerLocalizationHelperForMod(String modId, LocalizationHelper loc) {

    locHelpers.put(modId.toLowerCase(), loc);
  }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerLibF());
    MinecraftForge.EVENT_BUS.register(this);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    DataDump.dumpEnchantments();
    DataDump.dumpPotionEffects();
  }

  public static int getMCVersion() {

    return 11;
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

    EntityPlayer player = event.player;
    NBTTagCompound forgeData = player.getEntityData();
    if (!forgeData.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
      forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());

    NBTTagCompound persistedData = forgeData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    if (!persistedData.hasKey(NBT_ROOT_GUIDE_BOOKS))
      persistedData.setTag(NBT_ROOT_GUIDE_BOOKS, new NBTTagCompound());

    NBTTagCompound guideData = persistedData.getCompoundTag(NBT_ROOT_GUIDE_BOOKS);

    int id = 0;
    ItemGuideBookSL item = ItemGuideBookSL.getBookById(id);
    while (item != null && item.giveBookOnFirstLogin) {
      if (!guideData.getBoolean(item.getFullName())) {
        guideData.setBoolean(item.getFullName(), true);
        PlayerHelper.giveItem(player, new ItemStack(item));
        logHelper.info("Player has been given guide book " + item.getFullName());
      }

      item = ItemGuideBookSL.getBookById(++id);
    }
  }
}
