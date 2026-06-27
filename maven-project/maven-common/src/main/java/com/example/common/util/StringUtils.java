package com.example.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * 演示：compile scope 依赖（slf4j-api）在代码中的使用
 * logback-classic 是 runtime scope，这里不直接引用任何 logback 类
 */
public class StringUtils {

    // slf4j-api 是 compile scope（父模块 dependencies 里声明），直接可用
    // logback-classic 是 runtime scope，提供实际日志输出，但代码里不直接引用
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    /**
     * 判断字符串是否为空（null 或空字符串）
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 将驼峰命名转为下划线命名
     * 例：userName -> user_name
     */
    public static String camelToUnderscore(String camel) {
        if (isEmpty(camel)) {
            return camel;
        }
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
