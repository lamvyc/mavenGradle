# maven-common — 公共工具模块

## 是什么

所有子模块都能用的"工具箱"，避免重复写代码。

本模块同时演示 Maven 的**依赖范围（scope）**——同一个模块里，三个依赖用了三种不同的 scope：

---

## 三种 scope 在代码中的体现

### slf4j-api — compile（父模块继承）

**声明位置：** `maven-project/pom.xml` 的 `<dependencies>`，所有子模块自动继承，无需重复声明。

**代码体现：** `StringUtils.java` 里直接 import 了它：
```java
import org.slf4j.Logger;           // 能 import = 编译时就有它
import org.slf4j.LoggerFactory;
```

**结论：** 编译、运行、打包都有它。

---

### logback-classic — runtime

**声明位置：** `maven-common/pom.xml`，scope 从父模块 `dependencyManagement` 继承。

**代码体现：** `StringUtils.java` 里**找不到任何 `import ch.qos.logback...`** ——代码不引用它，但程序跑起来日志能正常输出，说明它在运行时被 JVM 自动加载。

**结论：** 编译时不需要，只运行时需要。logback 是 slf4j 的"幕后实现"。

---

### junit — test

**声明位置：** `maven-common/pom.xml`，scope=test 从父模块继承。

**代码体现：** `StringUtilsTest.java`（test 目录）里能 import 它：
```java
import org.junit.Test;   // 只有 src/test/java 里能用
```

在 `StringUtils.java`（main 目录）里写 `import org.junit.Test` 会**报红**——test scope 的依赖，main 代码用不了。

**结论：** 只在测试阶段可用，不打包进最终 jar。

---

## 包含什么

- `StringUtils` — isEmpty（判断空字符串）、camelToUnderscore（驼峰转下划线）

## 如何运行

```bash
# 在 maven-project 目录下执行

# 运行测试
mvn test -pl maven-common

# 只运行 StringUtilsTest
mvn test -pl maven-common -Dtest=StringUtilsTest

# 只运行某个方法
mvn test -pl maven-common -Dtest=StringUtilsTest#testCamelToUnderscore
```
