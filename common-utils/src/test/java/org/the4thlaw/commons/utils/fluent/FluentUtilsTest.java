package org.the4thlaw.commons.utils.fluent;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link FluentUtils}.
 */
 class FluentUtilsTest {
    /**
     * Tests {@link FluentUtils#endsWithAnyCI(String...)}.
     */
    @Test
    void endsWithAnyCI() {
        Predicate<String> endsWithAnyCI = FluentUtils.endsWithAnyCI("java", "class");
        assertThat(endsWithAnyCI.test("foo.java")).isTrue();
        assertThat(endsWithAnyCI.test("foo.JAVA")).isTrue();
        assertThat(endsWithAnyCI.test("foo.Class")).isTrue();
        assertThat(endsWithAnyCI.test("foo.clazz")).isFalse();
    }
}
