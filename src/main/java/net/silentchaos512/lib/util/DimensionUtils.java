package net.silentchaos512.lib.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.regex.Pattern;

public final class DimensionUtils {
    private static final Pattern NAME_PATTERN = Pattern.compile("^dim(ension)?_?");

    private DimensionUtils() {}

    public static boolean matches(DimensionType dimensionType, String input) {
        return dimensionType == from(input);
    }

    public static boolean containedInList(DimensionType dimensionType, Collection<? extends String> list, boolean valueIfListEmpty) {
        if (list.isEmpty()) return valueIfListEmpty;

        for (String str : list) {
            if (matches(dimensionType, str)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static DimensionType from(String str) {
        // Shortcut names
        if ("default".equalsIgnoreCase(str) || "overworld".equalsIgnoreCase(str))
            return DimensionType.OVERWORLD;
        if ("nether".equalsIgnoreCase(str))
            return DimensionType.NETHER;
        if ("end".equalsIgnoreCase(str))
            return DimensionType.THE_END;

        // Try to get by registry name
        ResourceLocation id = ResourceLocation.tryCreate(str);
        if (id != null) {
            DimensionType type = DimensionType.byName(id);
            if (type != null) {
                return type;
            }
        }

        // Get by integer ID
        String trimmed = NAME_PATTERN.matcher(str).replaceFirst("");
        try {
            int k = Integer.parseInt(str);
            return DimensionType.getById(k);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
