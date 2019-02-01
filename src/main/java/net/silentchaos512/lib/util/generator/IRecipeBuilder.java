package net.silentchaos512.lib.util.generator;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface IRecipeBuilder {
    JsonObject build();
}
