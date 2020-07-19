/*
 * Silent Lib -- MCMathUtils
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

import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3i;

public final class MCMathUtils {
    private MCMathUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Distance between two {@link Vector3i} (such as {@link net.minecraft.util.math.BlockPos}).
     * Consider using {@link #distanceSq} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to}
     */
    public static double distance(Vector3i from, Vector3i to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Distance between two {@link IPosition}. Consider using {@link #distanceSq} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to}
     */
    public static double distance(IPosition from, IPosition to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Distance between an entity's position and a given position. Consider using {@link
     * #distanceSq} when possible.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos}
     */
    public static double distance(Entity entity, Vector3i pos) {
        double dx = pos.getX() + 0.5 - entity.getPosX();
        double dy = pos.getY() + 0.5 - entity.getPosY();
        double dz = pos.getZ() + 0.5 - entity.getPosZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Distance between an entity's position and a given position. Consider using {@link
     * #distanceSq} when possible.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos}
     */
    public static double distance(Entity entity, IPosition pos) {
        double dx = pos.getX() - entity.getPosX();
        double dy = pos.getY() - entity.getPosY();
        double dz = pos.getZ() - entity.getPosZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Distance squared between two {@link Vector3i} (such as {@link net.minecraft.util.math.BlockPos}).
     * Use instead of {@link #distance} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared
     */
    public static double distanceSq(Vector3i from, Vector3i to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Distance squared between two {@link IPosition}. Use instead of {@link #distance} when
     * possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared
     */
    public static double distanceSq(IPosition from, IPosition to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Distance squared between an entity's position and a given position.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos} squared
     */
    public static double distanceSq(Entity entity, Vector3i pos) {
        double dx = pos.getX() + 0.5 - entity.getPosX();
        double dy = pos.getY() + 0.5 - entity.getPosY();
        double dz = pos.getZ() + 0.5 - entity.getPosZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Distance squared between an entity's position and a given position.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos} squared
     */
    public static double distanceSq(Entity entity, IPosition pos) {
        double dx = pos.getX() - entity.getPosX();
        double dy = pos.getY() - entity.getPosY();
        double dz = pos.getZ() - entity.getPosZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Distance between two {@link Vector3i} (such as {@link net.minecraft.util.math.BlockPos}), but
     * ignores the Y-coordinate. Consider using {@link #distanceHorizontalSq} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared, ignoring Y-axis
     */
    public static double distanceHorizontal(Vector3i from, Vector3i to) {
        int dx = to.getX() - from.getX();
        int dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Distance between two {@link IPosition}, but ignores the Y-coordinate. Consider using {@link
     * #distanceHorizontalSq} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared
     */
    public static double distanceHorizontal(IPosition from, IPosition to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Distance between an entity's position and a given position, but ignores the Y-coordinate.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos}, ignoring Y-axis
     */
    public static double distanceHorizontal(Entity entity, Vector3i pos) {
        double dx = pos.getX() + 0.5 - entity.getPosX();
        double dz = pos.getZ() + 0.5 - entity.getPosZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Distance between an entity's position and a given position, but ignores the Y-coordinate.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos}, ignoring Y-axis
     */
    public static double distanceHorizontal(Entity entity, IPosition pos) {
        double dx = pos.getX() - entity.getPosX();
        double dz = pos.getZ() - entity.getPosZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Distance squared between two {@link Vector3i} (such as {@link net.minecraft.util.math.BlockPos}),
     * but ignores the Y-coordinate. Use instead of {@link #distanceHorizontal} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared, ignoring Y-axis
     */
    public static double distanceHorizontalSq(Vector3i from, Vector3i to) {
        int dx = to.getX() - from.getX();
        int dz = to.getZ() - from.getZ();
        return dx * dx + dz * dz;
    }

    /**
     * Distance squared between two {@link IPosition}, but ignores the Y-coordinate. Use instead of
     * {@link #distanceHorizontal} when possible.
     *
     * @param from One point
     * @param to   Another point
     * @return The distance between {@code from} and {@code to} squared, ignoring Y-axis
     */
    public static double distanceHorizontalSq(IPosition from, IPosition to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        return dx * dx + dz * dz;
    }

    /**
     * Distance squared between an entity's position and a given position, but ignores the
     * Y-coordinate.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos} squared, ignoring Y-axis
     */
    public static double distanceHorizontalSq(Entity entity, Vector3i pos) {
        double dx = pos.getX() + 0.5 - entity.getPosX();
        double dz = pos.getZ() + 0.5 - entity.getPosZ();
        return dx * dx + dz * dz;
    }

    /**
     * Distance squared between an entity's position and a given position, but ignores the
     * Y-coordinate.
     *
     * @param entity The entity
     * @param pos    The other point
     * @return The distance between {code entity} and {@code pos} squared, ignoring Y-axis
     */
    public static double distanceHorizontalSq(Entity entity, IPosition pos) {
        double dx = pos.getX() - entity.getPosX();
        double dz = pos.getZ() - entity.getPosZ();
        return dx * dx + dz * dz;
    }
}
