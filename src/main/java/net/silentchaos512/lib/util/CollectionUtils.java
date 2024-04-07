package net.silentchaos512.lib.util;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CollectionUtils {
    private static final Random RANDOM = new Random();

    private CollectionUtils() {throw new IllegalAccessError("Utility class");}

    public static <T> T random(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T random(Stream<T> stream) {
        return random(stream.collect(Collectors.toList()));
    }
}
