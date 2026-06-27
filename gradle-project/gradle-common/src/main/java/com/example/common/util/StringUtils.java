package com.example.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * slf4j-api 是 implementation（来自根 build.gradle），可直接使用
 * logback-classic 是 runtimeOnly，提供日志输出实现，代码里不引用它的类
 */
public class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 驼峰转下划线：userName -> user_name
     */
    public static String camelToUnderscore(String camel) {
        if (isEmpty(camel)) return camel;
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        log.debug("camelToUnderscore: {} -> {}", camel, sb);
        return sb.toString();
    }
}
