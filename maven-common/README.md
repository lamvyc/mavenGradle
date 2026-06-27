# maven-common — 公共工具模块

## 这个模块是干什么的

存放所有子模块都可以复用的工具类。本模块同时演示 Maven 的**依赖范围（scope）**：

| 依赖 | scope | 含义 |
|------|-------|------|
| slf4j-api | compile（父模块继承） | 编译/运行都需要，打包进 jar |
| logback-classic | runtime | 仅运行时需要，代码不直接引用 logback 类 |
| junit | test | 仅测试阶段可用，不打包 |

## 包含什么

- `StringUtils` — 字符串工具（isEmpty、驼峰转下划线）

## 怎么运行/验证

```bash
# 在 maven-common 目录下执行

# 编译
mvn compile

# 运行单元测试
mvn test

# 只运行 StringUtilsTest
mvn test -Dtest=StringUtilsTest

# 只运行某个方法
mvn test -Dtest=StringUtilsTest#testCamelToUnderscore
```
