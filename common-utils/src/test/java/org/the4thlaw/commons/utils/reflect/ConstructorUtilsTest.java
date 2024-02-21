package org.the4thlaw.commons.utils.reflect;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConstructorUtils}.
 */
class ConstructorUtilsTest {
    @Test
    void getConstructorSupplier1Arg() {
        Function<String, RuntimeException> supplier = ConstructorUtils.getConstructorSupplier(RuntimeException.class,
                String.class);
        assertThat(supplier).isNotNull();
        RuntimeException instance = supplier.apply("Foo");
        assertThat(instance).isNotNull();
        assertThat(instance.getMessage()).isEqualTo("Foo");
    }
}
