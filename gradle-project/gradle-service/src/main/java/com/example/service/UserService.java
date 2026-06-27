package com.example.service;

import com.example.domain.User;
import com.example.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户业务服务
 * 演示跨模块调用：
 *   - User 来自 gradle-domain（通过 project(':gradle-domain') 依赖）
 *   - StringUtils 来自 gradle-common（通过 project(':gradle-common') 依赖）
 *   - slf4j-api 通过根 build.gradle subprojects 继承，无需单独声明
 */
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User findById(Long id) {
        log.info("查询用户，id={}", id);
        return new User(id, "alice", "alice@example.com");
    }

    public User register(String username, String email) {
        // StringUtils 来自 gradle-common 模块
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            throw new IllegalArgumentException("用户名和邮箱不能为空");
        }
        log.info("注册新用户：username={}", username);
        return new User(System.currentTimeMillis(), username, email);
    }
}
