package com.example.service;

import com.example.domain.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * UserService 单元测试
 * 演示：
 *   1. @Before：每个测试方法执行前运行（JUnit4），JUnit5 用 @BeforeEach
 *   2. 测试命名：testXxx 或 方法名_场景_预期结果
 *   3. 常用断言：assertEquals、assertNotNull、assertTrue、assertThrows（JUnit5）
 *
 * 常用命令：
 *   mvn test                              # 运行全部测试
 *   mvn test -Dtest=UserServiceTest       # 只运行此类
 *   mvn test -Dtest=UserServiceTest#testRegister  # 只运行一个方法
 */
public class UserServiceTest {

    private UserService userService;

    // @Before：测试前初始化，避免测试间相互影响
    @Before
    public void setUp() {
        userService = new UserService();
    }

    @Test
    public void testFindById() {
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals(Long.valueOf(1L), user.getId());
        assertEquals("alice", user.getUsername());
    }

    @Test
    public void testRegister_success() {
        User user = userService.register("charlie", "charlie@example.com");
        assertNotNull(user);
        assertEquals("charlie", user.getUsername());
        assertEquals("charlie@example.com", user.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegister_emptyUsername_throwsException() {
        // 传入空用户名，期望抛出 IllegalArgumentException
        // JUnit4 用 @Test(expected=...)，JUnit5 用 assertThrows
        userService.register("", "test@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegister_nullEmail_throwsException() {
        userService.register("bob", null);
    }
}
