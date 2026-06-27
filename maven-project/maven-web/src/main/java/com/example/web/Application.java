package com.example.web;

import com.example.domain.User;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用入口
 * 演示：
 *   1. web → service → domain, common 的完整依赖链
 *   2. web 模块可以直接使用 domain 和 common 的类（通过依赖传递）
 *   3. mvn package 后可用 java -jar 运行
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("应用启动...");

        UserService userService = new UserService();

        // 通过 service 查询 user（user 类来自 maven-domain，通过传递依赖获得）
        User user = userService.findById(1L);
        log.info("查询到用户：{}", user);

        // 注册新用户
        User newUser = userService.register("bob", "bob@example.com");
        log.info("注册成功：{}", newUser);
    }
}
