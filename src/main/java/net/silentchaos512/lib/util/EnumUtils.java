/*
 * Silent Lib -- EnumUtils
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.util;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Some generic utility methods for working with enums.
 *
 * @since 3.0.7
 */
public final class EnumUtils {
    private EnumUtils() {throw new IllegalAccessError("Utility class");}

    /**
     * Get an enum constant that produces the given value from {@code getter}. Returns the first
     * result, even if multiple constants would match.
     *
     * @return The first match, if one exists, or defaultValue otherwise.
     */
    public static <E extends Enum<E>> E byIndex(int value, E defaultValue, Function<E, Integer> getter) {
        for (E e : defaultValue.getDeclaringClass().getEnumConstants()) {
            if (getter.apply(e) == value) {
                return e;
            }
        }
        return defaultValue;
    }

    /**
     * Get the enum constant with the given name (ignoring case), or {@code defaultValue} if no
     * match is found.
     *
     * @return The enum constant with the given name, or {@code defaultValue} if invalid.
     */
    public static <E extends Enum<E>> E byName(String name, E defaultValue) {
        for (E e : defaultValue.getDeclaringClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return defaultValue;
    }

    /**
     * Get the enum constant with the given ordinal, or {@code defaultValue} if out-of-bounds.
     *
     * @return The enum constant with the given ordinal, or {@code defaultValue} if ordinal is not
     * valid.
     */
    public static <E extends Enum<E>> E byOrdinal(int ordinal, E defaultValue) {
        E[] enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
        }
        return defaultValue;
    }

    /**
     * Check the object is a valid constant of the enum class, ignoring case.
     *
     * @return True if obj is non-null and {@code obj.toString()} matches a constant in the enum
     * class (ignoring case), false otherwise.
     */
    public static <E extends Enum<E>> boolean validate(@Nullable Object obj, Class<E> enumClass) {
        if (obj != null) {
            for (E e : enumClass.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(obj.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Temporary workaround for enum issue in Forge build 83.
     */
    public static <E extends Enum<E>> Supplier<E> defineEnumFix(ForgeConfigSpec.Builder builder, String path, E defaultValue) {
        ForgeConfigSpec.ConfigValue<String> configValue = builder
                .define(path, defaultValue::name, o -> validate(o, defaultValue.getDeclaringClass()));
        return () -> byName(configValue.get(), defaultValue);
    }

    @SuppressWarnings("SuspiciousMethodCalls") // for validValues.contains
    public static <E extends Enum<E>> Supplier<E> defineEnumFix(ForgeConfigSpec.Builder builder, String path, E defaultValue, Set<E> validValues) {
        ForgeConfigSpec.ConfigValue<String> configValue = builder
                .define(path, defaultValue::name, o ->
                        validate(o, defaultValue.getDeclaringClass()) && validValues.contains(o));
        return () -> byName(configValue.get(), defaultValue);
    }
}
