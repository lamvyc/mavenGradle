package com.example.service;

import com.example.domain.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 运行方式：
 *   ./gradlew :gradle-service:test
 *   ./gradlew :gradle-service:test --tests "*.UserServiceTest"
 *   ./gradlew :gradle-service:test --tests "*.UserServiceTest.testRegister_success"
 *
 * 测试报告：build/reports/tests/test/index.html（用浏览器打开）
 */
public class UserServiceTest {

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService();
    }

    @Test
    public void testFindById() {
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals(Long.valueOf(1L), user.getId());
    }

    @Test
    public void testRegister_success() {
        User user = userService.register("bob", "bob@example.com");
        assertEquals("bob", user.getUsername());
        assertEquals("bob@example.com", user.getEmail());
    }

    // @Test(expected=...) 是 JUnit4 验证异常的方式
    // JUnit5 改用 assertThrows(() -> ...)
    @Test(expected = IllegalArgumentException.class)
    public void testRegister_emptyUsername_throwsException() {
        userService.register("", "test@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegister_nullEmail_throwsException() {
        userService.register("bob", null);
    }
}
