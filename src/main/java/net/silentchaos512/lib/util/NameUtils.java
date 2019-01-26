package net.silentchaos512.lib.util;

import java.util.regex.Pattern;

public class NameUtils {
    private static final Pattern PATTERN = Pattern.compile("([a-z0-9/._-]+:)?[a-z0-9/._-]+");

    public static boolean isValid(String name) {
        return PATTERN.matcher(name).matches();
    }
}
