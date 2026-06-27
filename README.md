# Maven & Gradle 最佳实践项目

配套文档：[maven-best-practices.md](./maven-best-practices.md) | [gradle-best-practices.md](./gradle-best-practices.md) | [rule.md](./rule.md)

## Maven 项目结构

```
mavenGradle/
├── pom.xml                                          # 父模块：聚合 + dependencyManagement 版本锁定
├── maven-common/                                    # 公共工具模块（演示 scope）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/common/
│       │   └── util/
│       │       └── StringUtils.java                # compile/runtime scope 使用示例
│       └── test/java/com/example/common/
│           └── util/
│               └── StringUtilsTest.java            # JUnit4 单元测试
├── maven-domain/                                    # 实体模块（零外部依赖）
│   ├── pom.xml
│   └── src/
│       └── main/java/com/example/domain/
│           └── User.java                           # 纯 POJO
├── maven-service/                                   # 业务逻辑模块（演示模块间依赖、依赖传递）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/service/
│       │   └── UserService.java                    # 跨模块调用 domain + common
│       └── test/java/com/example/service/
│           └── UserServiceTest.java                # @Before / @Test / expected 异常测试
└── maven-web/                                       # 入口模块（演示依赖排除、可执行 jar）
    ├── pom.xml
    └── src/
        └── main/java/com/example/web/
            └── Application.java                    # main 方法入口
```

## Maven 模块依赖关系

```
maven-web
  └── maven-service
        ├── maven-domain
        └── maven-common
```

## Maven 快速开始

```bash
# 全量构建（根目录执行）
mvn clean install

# 运行所有测试
mvn test

# 查看依赖树
mvn dependency:tree

# 运行入口（构建后）
java -jar maven-web/target/maven-web-1.0-SNAPSHOT.jar
```

---

## Gradle 项目结构

```
mavenGradle/
└── gradle-project/
    ├── settings.gradle                              # 声明所有子模块（等同于 Maven <modules>）
    ├── build.gradle                                 # 根配置：subprojects 公共配置 + 版本变量
    ├── gradle/wrapper/
    │   └── gradle-wrapper.properties               # 指定 Gradle 版本（推荐用 Wrapper）
    ├── gradle-common/                               # 公共工具模块（演示 runtimeOnly）
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/example/common/
    │       │   └── util/
    │       │       └── StringUtils.java
    │       └── test/java/com/example/common/
    │           └── util/
    │               └── StringUtilsTest.java
    ├── gradle-domain/                               # 实体模块（零额外依赖）
    │   ├── build.gradle
    │   └── src/
    │       └── main/java/com/example/domain/
    │           └── User.java
    ├── gradle-service/                              # 业务模块（演示 implementation vs api）
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/example/service/
    │       │   └── UserService.java
    │       └── test/java/com/example/service/
    │           └── UserServiceTest.java
    └── gradle-web/                                  # 入口模块（fat jar 打包）
        ├── build.gradle
        └── src/
            └── main/java/com/example/web/
                └── Application.java
```

## Gradle 模块依赖关系

```
gradle-web
  └── gradle-service
        ├── gradle-domain
        └── gradle-common
```

## Gradle 快速开始

```bash
cd gradle-project

# 全量构建
./gradlew clean build

# 运行所有测试
./gradlew test

# 查看依赖树
./gradlew dependencies --configuration runtimeClasspath

# 运行入口（构建后）
java -jar gradle-web/build/libs/gradle-web-1.0-SNAPSHOT.jar
```
