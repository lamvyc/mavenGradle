package com.example.common.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * StringUtils 单元测试
 * 演示：
 *   1. JUnit 是 test scope，只在测试阶段可用
 *   2. 测试类放在 src/test/java，与主代码分离
 *   3. mvn test 命令运行所有测试
 *   4. mvn test -Dtest=StringUtilsTest 只运行此类
 */
public class StringUtilsTest {

    // @Test 标记测试方法，方法名建议以 test 开头或描述性命名
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
        // 边界：空字符串原样返回
        assertEquals("", StringUtils.camelToUnderscore(""));
    }
}
