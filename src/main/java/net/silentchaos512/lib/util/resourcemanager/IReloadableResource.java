package net.silentchaos512.lib.util.resourcemanager;

import net.minecraft.util.ResourceLocation;

public interface IReloadableResource<T extends IReloadableResource> {
    ResourceLocation getId();

    IReloadableResourceSerializer<T> getSerializer();
}
