package org.the4thlaw.commons.utils.reflect;

import java.util.Objects;
import java.util.function.Predicate;

import org.the4thlaw.commons.utils.fluent.FluentUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to work with {@link Class}es.
 */
public final class ClassUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {
        // Utility class
    }

    /**
     * Finds the first class that has the provided name, is a subclass of the provided one.
     * 
     * @param <T> The type of the parent class.
     * @param parentClass The class object for the parent class.
     * @param names The names of the subclasses to check, in order.
     * @return A matching class object. If none of the names exist, the parent class is returned.
     */
    public static <T> Class<? extends T> findFirstChildClass(Class<T> parentClass, String... names) {
        return findFirstChildClass(parentClass, null, names);
    }

    /**
     * Finds the first class that has the provided name, is a subclass of the provided one and matches the provided
     * predicate (if any).
     * 
     * @param <T> The type of the parent class.
     * @param parentClass The class object for the parent class.
     * @param predicate A predicate to apply to potentially found classes. Can be {@code null}.
     * @param names The names of the subclasses to check, in order.
     * @return A matching class object. If none of the names exist, the parent class is returned.
     */
    public static <T> Class<? extends T> findFirstChildClass(Class<T> parentClass,
            Predicate<Class<? extends T>> predicate, String... names) {
        Objects.requireNonNull(parentClass, "parentClass cannot be null");

        if (predicate == null) {
            predicate = x -> true;
        }

        for (String name : names) {
            Class<? extends T> child = findChildClass(parentClass, name);
            if (child != null && predicate.test(child)) {
                return child;
            }
        }

        return parentClass;
    }

    /**
     * Finds a class that has the provided name and is also a subclass of the provided one.
     * 
     * @param <T> The type of the parent class.
     * @param name The name of the subclass.
     * @param parentClass The class object for the parent class.
     * @return The subclass object if it exists and is a subclass of the provided parent, or {@code null} if no such
     *         class exists.
     */
    public static <T> Class<? extends T> findChildClass(Class<T> parentClass, String name) {
        Class<?> found;
        try {
            found = FluentUtils.class.getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            LOGGER.debug("{} does not exist", name);
            return null;
        }
        if (parentClass.isAssignableFrom(found)) {
            LOGGER.debug("{} exists but does not extend {}", name, parentClass);
            return found.asSubclass(parentClass);
        }
        return null;
    }
}
