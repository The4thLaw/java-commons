package org.the4thlaw.commons.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * Utility class to work with {@link Constructor}s.
 */
public final class ConstructorUtils {
    private ConstructorUtils() {
        // Utility class
    }

    /**
     * Finds a constructor in a class and converts it to a lambda function that creates instances based on its
     * parameter.
     * 
     * @param <T> The type of the constructed class.
     * @param <P> The type of the single parameter for the constructor.
     * @param clazz The class object representing the constructed class.
     * @param parameterType The class object representing the single parameter for the constructor.
     * @return The lambda to create instances.
     */
    public static <T, P> Function<P, T> getConstructorSupplier(Class<T> clazz, Class<P> parameterType) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor(parameterType);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(
                    "Failed to find the constructor for " + clazz + " with parameter type " + parameterType);
        }

        return p -> {
            try {
                return constructor.newInstance(p);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException("Failed to get an instance of " + clazz + " with parameter " + p);
            }
        };
    }
}
