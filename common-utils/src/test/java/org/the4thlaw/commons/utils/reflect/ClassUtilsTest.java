package org.the4thlaw.commons.utils.reflect;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ClassUtils}.
 */
class ClassUtilsTest {
    private static final String NPE = "java.lang.NullPointerException";
    private static final String IAE = "java.lang.IllegalArgumentException";

    @Test
    void findFirstChildClass() {
        Predicate<Class<? extends RuntimeException>> predicate = c -> {
            try {
                c.getConstructor(String.class, Throwable.class);
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        };

        assertThat(ClassUtils.findFirstChildClass(RuntimeException.class, predicate, NPE, IAE))
                .isEqualTo(IllegalArgumentException.class);
        assertThat(ClassUtils.findFirstChildClass(RuntimeException.class, predicate, NPE))
                .isEqualTo(RuntimeException.class);
        assertThat(ClassUtils.findFirstChildClass(RuntimeException.class, predicate, "not.a.class"))
                .isEqualTo(RuntimeException.class);
    }
}
