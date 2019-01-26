package net.silentchaos512.lib.util.recipe;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface IRecipeBuilder {
    JsonObject build();
}
