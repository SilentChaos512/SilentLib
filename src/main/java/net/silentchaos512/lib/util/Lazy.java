package net.silentchaos512.lib.util;

import java.util.function.Supplier;

/**
 * A lazy object, very similar to {@link net.minecraft.util.LazyLoadedValue}. Use the {@link #of}
 * methods to create instances. Lazy objects store a {@link Supplier} at first, then the object
 * produced by the {@link Supplier}. Call {@link #get} to acquire the object.
 *
 * @param <T> The type of the stored object
 */
public class Lazy<T> {
    private T value;
    private Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Create a Lazy object, which will produce an object from the {@link Supplier} the first time
     * {@link #get} is called.
     *
     * @param supplier The function to produce the object
     * @param <T>      The type of the object
     * @return A new Lazy instance
     */
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     * Create a Lazy object, but with an already created object. This is a shortcut for {@link
     * #of(Supplier)} which is useful in some cases.
     *
     * @param obj The object
     * @param <T> The type of the object
     * @return A new Lazy instance
     */
    public static <T> Lazy<T> of(T obj) {
        return new Lazy<>(() -> obj);
    }

    /**
     * Gets the stored object. If this is the first time {@code get} is being called, this will
     * create the object from a {@link Supplier}.
     *
     * @return The stored (possibly newly created) object
     */
    public T get() {
        if (supplier != null) {
            value = supplier.get();
            supplier = null;
        }
        return value;
    }
}
