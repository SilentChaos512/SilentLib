/*
 * Silent Lib
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

package net.silentchaos512.lib.config;

import net.silentchaos512.lib.util.LogHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigLineParser {

    private static Map<Class<?>, Function<String, ?>> parsers = new HashMap<>();

    static {
        parsers.put(Integer.class, ConfigLineParser::parseInt);
        parsers.put(Float.class, ConfigLineParser::parseFloat);
    }

    private ConfigLineParser() {
        throw new IllegalAccessError("Utility class");
    }

    public static <T, U> List<Tuple2<T, U>> parse(String[] lines, String delimiter, String configId, LogHelper log) {
        List<Tuple2<T, U>> list = new ArrayList<>();
        for (String line : lines) {
            T first;
            U second;
            String[] elements = line.split(delimiter);

            // Check number of elements
            if (elements.length < 2) {
                log.warn("ConfigLineParser: Too few elements in line \"{}\". Ignoring entire line. Fix your config file!");
                continue;
            } else if (elements.length > 2) {
                log.warn("ConfigLineParser: Too many elements in line \"{}\". Ignoring extra values. Fix your config file!");
            }

//            first = elements[0];
//            second = elements[1];
        }
        return list;
    }

    @Nullable
    private static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Nullable
    private static Float parseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static class Tuple2<T, U> {
        public final T first;
        public final U second;

        public Tuple2(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class Tuple3<T, U, V> {
        public final T first;
        public final U second;
        public final V third;

        public Tuple3(T first, U second, V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public static class Tuple4<T, U, V, W> {
        public final T first;
        public final U second;
        public final V third;
        public final W fourth;

        public Tuple4(T first, U second, V third, W fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }
    }
}
