# gradle-common — 公共工具模块

## 是什么

存放所有子模块可复用的工具类。同时演示 Gradle 依赖范围：

| 依赖 | configuration | 说明 |
|------|--------------|------|
| slf4j-api | implementation（根配置继承） | 编译+运行都需要，打包进 jar |
| logback-classic | runtimeOnly | 只在运行时需要，代码不直接引用 logback 类 |
| junit | testImplementation（根配置继承） | 仅测试，不打包 |

## 包含什么

- `StringUtils` — isEmpty、驼峰转下划线

## 如何运行/调试

```bash
# 在 gradle-project 目录下执行

# 只编译本模块
./gradlew :gradle-common:compileJava

# 运行所有测试
./gradlew :gradle-common:test

# 运行指定测试类
./gradlew :gradle-common:test --tests "*.StringUtilsTest"

# 运行指定测试方法
./gradlew :gradle-common:test --tests "*.StringUtilsTest.testCamelToUnderscore"

# 查看测试报告（用浏览器打开）
open gradle-common/build/reports/tests/test/index.html
```
