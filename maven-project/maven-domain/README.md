# maven-domain — 实体模块

## 是什么

存放业务实体类（POJO）。POJO 就是只有字段和 get/set 方法的简单 Java 类，没有任何业务逻辑。

本模块的 `User.java` 就是：只有 id、username、email 三个字段，以及对应的 getter/setter。

---

## 为什么单独放一个模块

**防止循环依赖。**

如果把 `User` 放在 `maven-service` 里，当 `maven-common` 也需要用 `User` 时：

```
common 要用 User → 依赖 service
service          → 依赖 common
结果：common → service → common → ...（死循环，Maven 报错）
```

把 `User` 单独放在 `domain`，它不依赖任何人，所有模块都可以安全引用它：

```
           domain（不依赖任何人）
             ↑            ↑
          common        service
```

---

## 包含什么

- `User` — 用户实体（id、username、email）

---

## 哪里引用了它

**1. maven-service/pom.xml（直接依赖）**
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>maven-domain</artifactId>
    <version>${project.version}</version>
</dependency>
```

**2. maven-service/src/main/java/com/example/service/UserService.java（代码中 import）**
```java
import com.example.domain.User;   // 直接使用 User 实体
```

**3. maven-web（通过依赖传递间接获得）**
`maven-web` 依赖 `maven-service`，`maven-service` 依赖 `maven-domain`，
所以 `maven-web` 里也能直接用 `User`，不需要在 `maven-web/pom.xml` 里单独声明。

见：`maven-web/src/main/java/com/example/web/Application.java`
```java
User user = userService.findById(1L);   // User 类通过依赖传递获得
```

## 如何运行

```bash
# 在 maven-project 目录下执行

# 编译验证
mvn compile -pl maven-domain

# 安装到本地仓库（其他模块依赖它之前需先执行一次）
mvn install -pl maven-domain
```
