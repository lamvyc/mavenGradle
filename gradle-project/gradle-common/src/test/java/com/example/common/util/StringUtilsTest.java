package com.example.common.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 运行方式：
 *   ./gradlew :gradle-common:test
 *   ./gradlew :gradle-common:test --tests "*.StringUtilsTest"
 *   ./gradlew :gradle-common:test --tests "*.StringUtilsTest.testCamelToUnderscore"
 */
public class StringUtilsTest {

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("hello"));
    }

    @Test
    public void testCamelToUnderscore() {
        assertEquals("user_name", StringUtils.camelToUnderscore("userName"));
        assertEquals("my_class_name", StringUtils.camelToUnderscore("myClassName"));
        assertEquals("", StringUtils.camelToUnderscore(""));
    }
}
