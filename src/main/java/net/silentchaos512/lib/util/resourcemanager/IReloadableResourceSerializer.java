package net.silentchaos512.lib.util.resourcemanager;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IReloadableResourceSerializer<T extends IReloadableResource> {
    T read(ResourceLocation id, JsonObject json);

    T read(ResourceLocation id, PacketBuffer buffer);

    void write(PacketBuffer buffer, T obj);

    ResourceLocation getSerializerName();
}
