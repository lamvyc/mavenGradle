package com.example.service;

import com.example.domain.User;
import com.example.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户业务服务
 * 演示：
 *   1. 跨模块引用：使用 maven-domain 的 User，maven-common 的 StringUtils
 *   2. slf4j-api 通过 maven-common 的依赖传递获得，service 无需单独声明
 *   3. logback-classic 通过 maven-common 以 runtime 范围传递，代码不直接引用
 */
public class UserService {

    // slf4j-api 是通过 maven-common 传递的 compile 依赖，可直接使用
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 根据 id 获取用户（模拟实现，实际应查询数据库）
     */
    public User findById(Long id) {
        log.info("查询用户，id={}", id);
        // 模拟数据，实际项目中这里会通过 JDBC/ORM 查询
        return new User(id, "alice", "alice@example.com");
    }

    /**
     * 注册新用户
     * 演示 StringUtils 的使用（来自 maven-common 模块）
     */
    public User register(String username, String email) {
        // 使用 maven-common 提供的工具方法
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            throw new IllegalArgumentException("用户名和邮箱不能为空");
        }
        log.info("注册新用户：username={}", username);
        return new User(System.currentTimeMillis(), username, email);
    }
}
