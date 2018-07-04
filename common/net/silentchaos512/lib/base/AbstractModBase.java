package net.silentchaos512.lib.base;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.proxy.IProxy;
import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Draft for a mod class base. Not tested. Will likely be used in 1.13 if it works out.
 *
 * @author SilentChaos512
 */
public abstract class AbstractModBase implements IModBase {

    public final LocalizationHelper localization;
    public final LogHelper log;
    public final Random random;
    public final SRegistry registry;
    public IProxy proxy;
    public ConfigBase config;

    public AbstractModBase() {
        this.random = new Random();
        this.log = new LogHelper(getModName(), getBuildNum());
        this.registry = new SRegistry(getModId(), this.log);
        this.localization = new LocalizationHelper(getModId()).setReplaceAmpersand(true);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SilentLib.instance.registerLocalizationHelperForMod(getModId(), this.localization);

        this.config = getConfig();
        if (this.config != null) this.config.init(event.getSuggestedConfigurationFile());

        addPhasedInitializers();
        addRegistrationHandlers();

        onPreInit(event);

        this.proxy.preInit(this.registry, event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        onInit(event);
        if (this.config != null) this.config.save();
        proxy.init(this.registry, event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        onPostInit(event);
        proxy.postInit(this.registry, event);
    }

    protected abstract void addPhasedInitializers();

    protected abstract void addRegistrationHandlers();

    protected abstract void onPreInit(FMLPreInitializationEvent event);

    protected abstract void onInit(FMLInitializationEvent event);

    protected abstract void onPostInit(FMLPostInitializationEvent event);

    @Nullable
    protected abstract ConfigBase getConfig();
}
