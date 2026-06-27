# Maven 最佳实践设计文档

---

## 目录

1. [IDEA 集成 Maven](#1-idea-集成-maven)
2. [Maven 依赖管理](#2-maven-依赖管理)
3. [Maven 生命周期与常用命令](#3-maven-生命周期与常用命令)
4. [Maven 依赖爆红常见问题](#4-maven-依赖爆红常见问题)
5. [单元测试](#5-单元测试)
6. [分模块设计与开发](#6-分模块设计与开发)
7. [继承与聚合](#7-继承与聚合)

---

## 1. IDEA 集成 Maven

### 1.1 全局配置（首先做这步）

**路径：** `File → New Projects Setup → Settings for New Projects → Build Tools → Maven`

| 配置项 | 说明 | 推荐值 |
|--------|------|--------|
| Maven home path | Maven 安装路径 | 本地安装目录，如 `/usr/local/maven` |
| User settings file | 自定义 settings.xml | `~/.m2/settings.xml` |
| Local repository | 本地仓库路径 | `~/.m2/repository` 或自定义 |

> 全局配置保证新建项目自动使用正确的 Maven，避免每次手动设置。

---

### 1.2 创建 Maven 项目

**步骤：**
1. `File → New → Project`
2. 选择 `Maven Archetype`
3. 填写 `GroupId`（如 `com.example`）、`ArtifactId`（如 `my-app`）
4. 选择 Archetype（普通项目选 `maven-archetype-quickstart`，Web 项目选 `maven-archetype-webapp`）
5. 点击 `Create`

---

### 1.3 导入已有 Maven 项目

**步骤：**
1. `File → Open`，选择项目根目录（包含 `pom.xml` 的目录）
2. IDEA 自动识别为 Maven 项目
3. 若未自动识别，右键 `pom.xml → Add as Maven Project`

---

### 1.4 当前项目 Maven 配置

**路径：** `File → Settings → Build, Execution, Deployment → Build Tools → Maven`

- 可覆盖全局配置，仅对当前项目生效

---

## 2. Maven 依赖管理

### 2.1 依赖配置

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.20</version>
    <scope>compile</scope>  <!-- 依赖范围 -->
    <optional>false</optional>
    <exclusions>
        <!-- 排除传递依赖 -->
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

### 2.2 依赖范围（scope）

| scope | 编译 | 测试 | 运行时 | 说明 |
|-------|------|------|--------|------|
| `compile`（默认） | ✓ | ✓ | ✓ | 所有阶段都需要，会打包进 jar |
| `test` | ✗ | ✓ | ✗ | 仅测试，如 JUnit，不打包 |
| `provided` | ✓ | ✓ | ✗ | 容器已提供，如 servlet-api，不打包 |
| `runtime` | ✗ | ✓ | ✓ | 仅运行时需要，如 JDBC 驱动 |
| `system` | ✓ | ✓ | ✗ | 本地 jar，不推荐使用 |

---

### 2.3 依赖传递

当 A 依赖 B，B 依赖 C，A 会自动获得 C（传递依赖）。

**传递依赖规则（scope 降级）：**

| 直接依赖 ↓ / 传递依赖 → | compile | test | provided | runtime |
|------------------------|---------|------|----------|---------|
| **compile** | compile | — | — | runtime |
| **test** | test | — | — | test |
| **provided** | provided | — | — | provided |
| **runtime** | runtime | — | — | runtime |

> `test` 和 `provided` 的传递依赖不会继续传递（表中 `—` 表示不传递）。

**解决传递依赖冲突的两个原则：**
1. **最短路径优先**：依赖层级越浅，优先级越高
2. **同层级先声明优先**：同级时，`pom.xml` 中先写的依赖优先

---

### 2.4 排除传递依赖

当传递依赖引入了冲突版本时，手动排除：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

---

## 3. Maven 生命周期与常用命令

### 3.1 三套生命周期

Maven 有三套独立的生命周期，每套都从头执行：

| 生命周期 | 常用阶段 | 说明 |
|---------|---------|------|
| **clean** | pre-clean → clean → post-clean | 清理构建产物 |
| **default** | validate → compile → test → package → verify → install → deploy | 主构建流程 |
| **site** | pre-site → site → post-site → site-deploy | 生成项目文档 |

> 执行某个阶段时，该生命周期中它之前的所有阶段都会先执行。
> 例如执行 `mvn package`，会先执行 validate → compile → test → package。

---

### 3.2 日常开发常用命令

```bash
# ===== 清理 =====
mvn clean                        # 删除 target 目录

# ===== 编译 =====
mvn compile                      # 编译 src/main/java
mvn test-compile                 # 编译 src/test/java

# ===== 测试 =====
mvn test                         # 运行所有单元测试
mvn test -Dtest=MyTest           # 运行指定测试类
mvn test -Dtest=MyTest#myMethod  # 运行指定测试方法
mvn test -DskipTests=false       # 强制执行测试

# ===== 打包 =====
mvn package                      # 打包（jar/war）到 target/
mvn package -DskipTests          # 跳过测试直接打包

# ===== 安装到本地仓库 =====
mvn install                      # 打包并安装到 ~/.m2/repository
mvn install -DskipTests          # 跳过测试安装

# ===== 发布到远程仓库 =====
mvn deploy                       # 发布到远程仓库（需配置 distributionManagement）

# ===== 组合命令（最常用）=====
mvn clean package                # 清理后打包
mvn clean install                # 清理后安装到本地仓库
mvn clean install -DskipTests    # 清理后跳过测试安装

# ===== 依赖分析 =====
mvn dependency:tree              # 查看依赖树
mvn dependency:tree -Dincludes=groupId:artifactId  # 过滤指定依赖
mvn dependency:analyze           # 分析依赖使用情况（找出未使用/缺失的依赖）
mvn dependency:list              # 列出所有依赖
mvn dependency:resolve           # 下载所有依赖到本地仓库

# ===== 其他 =====
mvn versions:display-dependency-updates   # 检查依赖是否有新版本
mvn help:effective-pom           # 查看最终生效的 pom（含继承合并后）
mvn help:effective-settings      # 查看最终生效的 settings.xml
```

---

### 3.3 多模块项目命令

```bash
# 在父模块根目录执行，自动按依赖顺序构建所有子模块
mvn clean install

# 只构建指定模块及其依赖
mvn clean install -pl module-a -am

# 跳过某个模块
mvn clean install -pl !module-a
```

---

## 4. Maven 依赖爆红常见问题

### 问题 1：依赖下载失败（红色波浪线）

**原因：** 网络问题、仓库地址错误、版本不存在

**解决步骤：**
1. 检查 `settings.xml` 中的镜像配置，推荐使用阿里云镜像：
   ```xml
   <mirror>
       <id>aliyun</id>
       <mirrorOf>central</mirrorOf>
       <name>阿里云公共仓库</name>
       <url>https://maven.aliyun.com/repository/public</url>
   </mirror>
   ```
2. 执行 `mvn dependency:resolve` 或点击 IDEA Maven 面板的刷新按钮
3. 确认版本号在 [Maven Central](https://search.maven.org) 中存在

---

### 问题 2：本地仓库中存在 `.lastUpdated` 文件导致无法下载

**原因：** 上次下载失败留下了 `.lastUpdated` 标记文件，Maven 认为已尝试过，不再重新下载。

**解决步骤：**
```bash
# 删除所有 .lastUpdated 文件（Mac/Linux）
find ~/.m2/repository -name "*.lastUpdated" -delete

# Windows PowerShell
Get-ChildItem -Path "$env:USERPROFILE\.m2\repository" -Filter "*.lastUpdated" -Recurse | Remove-Item
```
删除后重新 `mvn clean install` 或刷新 IDEA Maven。

---

### 问题 3：依赖版本冲突

**原因：** 多个依赖传递了同一 jar 的不同版本。

**解决步骤：**
1. 用 `mvn dependency:tree` 找出冲突的版本
2. 在 `dependencyManagement` 中锁定版本（见第 7 节），或使用 `<exclusion>` 排除旧版

---

### 问题 4：IDEA 中 pom.xml 不报错但代码爆红

**解决步骤（按顺序尝试）：**
1. Maven 面板 → 点击刷新（Reload All Maven Projects）
2. `File → Invalidate Caches → Invalidate and Restart`
3. 删除 IDEA 缓存目录后重启（`~/.cache/JetBrains/` 或 `~/Library/Caches/JetBrains/`）
4. 检查 Project SDK 是否配置正确（`File → Project Structure → SDKs`）

---

### 问题 5：Jar 包下载不完整（corrupt jar）

**现象：** 编译报 `zip file is empty` 或 `invalid LOC header`

**解决步骤：**
1. 找到报错的 jar 在本地仓库的路径
2. 删除整个该版本目录（如 `~/.m2/repository/org/springframework/spring-context/5.3.20/`）
3. 重新 `mvn clean install`

---

## 5. 单元测试

### 5.1 依赖配置

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>

<!-- 或 JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

---

### 5.2 测试类结构

```java
// src/test/java/com/example/UserServiceTest.java
public class UserServiceTest {

    @BeforeEach  // JUnit5，JUnit4 用 @Before
    void setUp() {
        // 每个测试方法执行前初始化
    }

    @Test
    void testSomeMethod() {
        // Arrange
        // Act
        // Assert
        assertEquals(expected, actual);
    }

    @AfterEach  // JUnit5，JUnit4 用 @After
    void tearDown() {
        // 每个测试方法执行后清理
    }
}
```

---

### 5.3 maven-surefire-plugin 配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <!-- 跳过测试 -->
        <skipTests>false</skipTests>
        <!-- 测试失败继续构建（不推荐常规使用）-->
        <!-- <testFailureIgnore>true</testFailureIgnore> -->
        <!-- 指定测试文件匹配规则 -->
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

---

## 6. 分模块设计与开发

### 6.1 为什么分模块

- 职责单一，代码边界清晰
- 模块可独立编译、测试、发布
- 避免不同业务模块相互污染

---

### 6.2 典型分模块结构

```
my-project/                    ← 父模块（聚合）
├── pom.xml                    ← 父 pom（packaging = pom）
├── my-common/                 ← 公共工具模块
│   ├── pom.xml
│   └── src/
├── my-domain/                 ← 业务实体模块
│   ├── pom.xml
│   └── src/
├── my-service/                ← 业务逻辑模块（依赖 domain）
│   ├── pom.xml
│   └── src/
└── my-web/                    ← Web 入口模块（依赖 service）
    ├── pom.xml
    └── src/
```

---

### 6.3 创建子模块步骤

**步骤 1：** 父模块 `pom.xml` 设置 `packaging` 为 `pom`

```xml
<packaging>pom</packaging>
```

**步骤 2：** 父模块声明子模块（聚合，见第 7 节）

**步骤 3：** 在 IDEA 中右键父模块 → `New → Module`，选择 Maven，填写子模块的 `artifactId`

**步骤 4：** 子模块 `pom.xml` 声明父模块

```xml
<parent>
    <groupId>com.example</groupId>
    <artifactId>my-project</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
```

**步骤 5：** 子模块之间的依赖引用

```xml
<!-- my-service/pom.xml 中引用 my-domain -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-domain</artifactId>
    <version>${project.version}</version>
</dependency>
```

---

## 7. 继承与聚合

### 7.1 继承实现

**目的：** 子模块从父模块继承配置（依赖、插件、属性），避免重复。

**父模块 pom.xml：**

```xml
<groupId>com.example</groupId>
<artifactId>my-project</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>pom</packaging>   <!-- 父模块必须是 pom 类型 -->

<properties>
    <java.version>11</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**子模块 pom.xml：**

```xml
<parent>
    <groupId>com.example</groupId>
    <artifactId>my-project</artifactId>
    <version>1.0-SNAPSHOT</version>
    <!-- relativePath 默认为 ../pom.xml，可省略或显式写明 -->
    <relativePath>../pom.xml</relativePath>
</parent>

<!-- 子模块无需重复写 groupId、version，从父模块继承 -->
<artifactId>my-service</artifactId>
```

> 子模块会继承父模块的：`properties`、`dependencies`（直接依赖）、`dependencyManagement`、`plugins`、`pluginManagement`。

---

### 7.2 继承 - 版本锁定（dependencyManagement）

**目的：** 父模块统一管理依赖版本，子模块引用时无需写版本号，避免版本混乱。

**父模块 pom.xml：**

```xml
<dependencyManagement>
    <dependencies>
        <!-- 在这里声明版本，但不实际引入依赖 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.20</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.30</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**子模块 pom.xml（只需写 groupId + artifactId，版本从父模块继承）：**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <!-- 无需写 version，从父模块 dependencyManagement 继承 -->
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <!-- scope 也可以从 dependencyManagement 继承 -->
    </dependency>
</dependencies>
```

> **`dependencies` vs `dependencyManagement` 的区别：**
> - `dependencies`：声明即引入，子模块直接继承该依赖
> - `dependencyManagement`：只锁定版本，子模块需要时自行声明，不自动引入

---

### 7.3 聚合

**目的：** 在父模块中统一管理子模块的构建，执行一条命令构建所有子模块。

**父模块 pom.xml 添加 modules：**

```xml
<modules>
    <module>my-common</module>
    <module>my-domain</module>
    <module>my-service</module>
    <module>my-web</module>
</modules>
```

**构建顺序：** Maven 会自动根据子模块间的依赖关系确定构建顺序，无需手动排序。

**在父模块根目录执行：**

```bash
mvn clean install   # 按依赖顺序构建所有子模块
```

---

### 7.4 继承与聚合的关系

| | 继承 | 聚合 |
|--|------|------|
| **目的** | 统一配置，避免重复 | 统一构建，一键编译所有模块 |
| **配置位置** | 子模块 `<parent>` 指向父模块 | 父模块 `<modules>` 列出子模块 |
| **是否必须同时用** | 通常同时使用，但可以分开 | |

> 实践中，父模块通常同时承担继承和聚合两个职责。

---

## 附录：settings.xml 推荐配置

位置：`~/.m2/settings.xml`

```xml
<settings>
    <!-- 本地仓库路径 -->
    <localRepository>/Users/yourname/.m2/repository</localRepository>

    <!-- 阿里云镜像，加速依赖下载 -->
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>

    <!-- JDK 版本配置（可选，建议在 pom.xml 中配置） -->
    <profiles>
        <profile>
            <id>jdk-11</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>11</jdk>
            </activation>
            <properties>
                <maven.compiler.source>11</maven.compiler.source>
                <maven.compiler.target>11</maven.compiler.target>
                <maven.compiler.compilerVersion>11</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
</settings>
```
