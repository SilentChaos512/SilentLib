package net.silentchaos512.lib.util;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

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

    public static <E extends Enum<E>> Optional<E> byIndex(int value, Class<E> clazz, Function<E, Integer> getter) {
        for (E e : clazz.getEnumConstants()) {
            if (getter.apply(e) == value) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
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

    public static <E extends Enum<E>> Optional<E> byName(String name, Class<E> clazz) {
        for (E e : clazz.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
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

    public static <E extends Enum<E>> Optional<E> byOrdinal(int ordinal, Class<E> clazz) {
        E[] enumConstants = clazz.getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return Optional.of(enumConstants[ordinal]);
        }
        return Optional.empty();
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
     * Cycles an enum, returning either the next or previous constant, looping around if needed.
     *
     * @param current The current value
     * @param reverse If true, cycles backwards
     * @param <E>     The enum class
     * @return The next or previous enum constant
     */
    public static <E extends Enum<E>> E cycle(E current, boolean reverse) {
        int k = current.ordinal() + (reverse ? -1 : 1);
        int valuesCount = current.getClass().getEnumConstants().length;
        if (k < 0) {
            k = valuesCount - 1;
        } else if (k >= valuesCount) {
            k = 0;
        }
        return byOrdinal(k, current);
    }
}
