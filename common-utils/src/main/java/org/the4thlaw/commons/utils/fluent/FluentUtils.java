package org.the4thlaw.commons.utils.fluent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.the4thlaw.commons.utils.reflect.ClassUtils;
import org.the4thlaw.commons.utils.reflect.ConstructorUtils;

/**
 * Utilities to work with fluent APIs.
 */
public final class FluentUtils {
    private FluentUtils() {
        // Utility class
    }

    private static final Function<String, ? extends RuntimeException> entityNotFoundCreator;

    static {
        Class<? extends RuntimeException> exceptionClass = findExceptionClass();
        entityNotFoundCreator = ConstructorUtils.getConstructorSupplier(exceptionClass, String.class);
    }

    private static Class<? extends RuntimeException> findExceptionClass() {
        Predicate<Class<? extends RuntimeException>> constructorCheck = c -> {
            try {
                c.getConstructor(String.class);
            } catch (Exception e) {
                return false;
            }
            return true;
        };
        return ClassUtils.findFirstChildClass(RuntimeException.class, constructorCheck,
                "jakarta.persistence.EntityNotFoundException", "javax.persistence.EntityNotFoundException");
    }

    /**
     * Returns a supplier for exceptions when an item cannot be found by its ID.
     * 
     * <p>
     * The exception class is one of (depending on what exists in the classpath):
     * </p>
     * <ul>
     * <li>{@code jakarta.persistence.EntityNotFoundException}</li>
     * <li>{@code javax.persistence.EntityNotFoundException}</li>
     * <li>{@code java.lang.RuntimeException}</li>
     * </ul>
     * 
     * @param type The type of item.
     * @param id The ID that was searched for.
     * @return The exception supplier.
     */
    public static Supplier<? extends RuntimeException> notFoundById(Class<?> type, Object id) {
        return () -> entityNotFoundCreator.apply(type.getSimpleName() + " not found by ID: " + id);
    }

    /**
     * Returns a predicate that matches strings that end with any of the provided strings.
     * 
     * @param ends The potential suffixes.
     * @return The predicate.
     */
    public static Predicate<String> endsWithAnyCI(String... ends) {
        final String[] lcEnds = Stream.of(ends).map(e -> e.toLowerCase(Locale.getDefault())).toArray(String[]::new);
        return s -> {
            String lcS = s.toLowerCase(Locale.getDefault());
            return Stream.of(lcEnds).anyMatch(ext -> lcS.endsWith(ext));
        };
    }

    /**
     * Returns a predicate that matches regular files ending with the provided extension(s).
     * 
     * @param exts The file extensions.
     * @return The predicate.
     */
    public static Predicate<Path> fileWithExtension(String... exts) {
        Predicate<String> endsWith = endsWithAnyCI(exts);
        return p -> Files.isRegularFile(p) && endsWith.test(p.getFileName().toString());
    }
}
