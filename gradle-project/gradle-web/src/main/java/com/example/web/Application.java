package com.example.web;

import com.example.service.UserService;
import com.example.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用入口
 *
 * 构建并运行：
 *   ./gradlew :gradle-web:jar          # 打包（在 gradle-web/build/libs/ 下生成 jar）
 *   java -jar gradle-web/build/libs/gradle-web-1.0-SNAPSHOT.jar
 *
 * 或直接完整构建：
 *   ./gradlew clean build
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("应用启动...");

        UserService userService = new UserService();

        // gradle-web → gradle-service → gradle-domain 的完整调用链
        User user = userService.findById(1L);
        log.info("查询到用户：{}", user);

        User newUser = userService.register("charlie", "charlie@example.com");
        log.info("注册成功：{}", newUser);
    }
}
