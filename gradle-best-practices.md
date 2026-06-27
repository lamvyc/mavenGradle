# Gradle 最佳实践设计文档

> 遵循原则：[rule.md](./rule.md) — 不炫技，看懂学会为目标，分步骤，注释解释"为什么"

---

## 目录

1. [Gradle 是什么，和 Maven 有什么区别](#1-gradle-是什么和-maven-有什么区别)
2. [IDEA 集成 Gradle](#2-idea-集成-gradle)
3. [Gradle 核心概念](#3-gradle-核心概念)
4. [依赖管理](#4-依赖管理)
5. [日常常用命令](#5-日常常用命令)
6. [单元测试](#6-单元测试)
7. [分模块设计（多项目构建）](#7-分模块设计多项目构建)
8. [常见问题排查](#8-常见问题排查)

---

## 1. Gradle 是什么，和 Maven 有什么区别

Gradle 是一个构建工具，和 Maven 做的事情一样：管理依赖、编译代码、运行测试、打包。

| 对比项 | Maven | Gradle |
|--------|-------|--------|
| 配置文件 | `pom.xml`（XML） | `build.gradle`（Groovy/Kotlin） |
| 配置风格 | 声明式，固定结构 | 可编程，更灵活 |
| 构建速度 | 较慢 | 快（增量构建、缓存） |
| 学习曲线 | 平缓 | 稍陡（需理解 Groovy/DSL） |
| 使用场景 | Java 企业项目 | Android 项目、大型多模块项目 |

> 两者核心概念相通（依赖管理、生命周期、插件），学会 Maven 再学 Gradle 容易很多。

---

## 2. IDEA 集成 Gradle

### 2.1 全局配置（先做这步）

**路径：** `File → New Projects Setup → Settings for New Projects → Build Tools → Gradle`

| 配置项 | 推荐值 | 说明 |
|--------|--------|------|
| Gradle user home | `~/.gradle` | Gradle 缓存目录，类似 Maven 的 `~/.m2` |
| Use Gradle from | `Specified location` 或 `gradle-wrapper.properties` | 推荐用 Wrapper，见下文 |
| Build and run using | `Gradle` | 用 Gradle 运行，而不是 IDEA 内置编译 |

### 2.2 创建 Gradle 项目

**步骤：**
1. `File → New → Project`
2. 选择 `Gradle`，语言选 `Java`
3. 填写 `GroupId`、`ArtifactId`
4. Build script DSL 选 `Groovy`（初学者更易读）
5. 勾选 `Add sample code`
6. 点击 `Create`

### 2.3 导入已有 Gradle 项目

**步骤：**
1. `File → Open`，选择包含 `build.gradle` 的目录
2. IDEA 弹出提示，选择 `Open as Project`
3. 等待 Gradle 同步完成（右下角进度条）

> 若同步失败，检查网络或 Gradle Wrapper 版本（见第 8 节常见问题）

---

## 3. Gradle 核心概念

### 3.1 Gradle Wrapper（推荐使用）

Wrapper 是一个脚本，让项目携带指定版本的 Gradle，无需在机器上手动安装。

```
项目根目录/
├── gradlew          # Mac/Linux 执行脚本
├── gradlew.bat      # Windows 执行脚本
└── gradle/
    └── wrapper/
        └── gradle-wrapper.properties   # 指定 Gradle 版本
```

**使用 Wrapper 而不是全局 gradle 命令：**
```bash
./gradlew build    # Mac/Linux，用项目指定的 Gradle 版本
gradlew.bat build  # Windows
```

> 为什么用 Wrapper：团队成员不需要手动安装 Gradle，版本统一，不会出现"我这能跑你那不行"。

### 3.2 build.gradle 基本结构

```groovy
// 声明插件：java 插件提供 compile、test、jar 等任务
plugins {
    id 'java'
}

// 项目坐标，和 Maven 的 groupId:artifactId:version 对应
group = 'com.example'
version = '1.0-SNAPSHOT'

// Java 版本
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// 依赖仓库：告诉 Gradle 去哪里下载依赖
repositories {
    mavenCentral()   // 等同于 Maven Central
}

// 依赖声明
dependencies {
    implementation 'org.slf4j:slf4j-api:1.7.36'   // compile，打包
    testImplementation 'junit:junit:4.13.2'         // 仅测试
    runtimeOnly 'ch.qos.logback:logback-classic:1.2.11'  // 仅运行时
}
```

### 3.3 Gradle 依赖范围（Configuration）

Gradle 的依赖范围和 Maven scope 对应关系：

| Gradle | Maven 对应 | 说明 |
|--------|-----------|------|
| `implementation` | `compile` | 编译和运行都需要，打包进 jar，**不暴露给调用方** |
| `api` | `compile` | 同上，但**会暴露给调用方**（需 java-library 插件） |
| `testImplementation` | `test` | 仅测试阶段，不打包 |
| `runtimeOnly` | `runtime` | 仅运行时，如 JDBC 驱动 |
| `compileOnly` | `provided` | 仅编译，容器提供，如 servlet-api |

> `implementation` vs `api`：
> - 模块 A 用 `api` 依赖了 B，则依赖 A 的模块 C 也能直接用 B 的类
> - 模块 A 用 `implementation` 依赖了 B，则 C 看不到 B（需要自己声明）
> - 推荐默认用 `implementation`，减少不必要的传递

### 3.4 Task（任务）

Gradle 的执行单元叫 Task，类似 Maven 的 phase。

```bash
./gradlew tasks          # 查看所有可用 task
./gradlew help --task compileJava   # 查看某个 task 的说明
```

常用 Task（java 插件提供）：

| Task | 说明 |
|------|------|
| `compileJava` | 编译 src/main/java |
| `compileTestJava` | 编译 src/test/java |
| `test` | 运行单元测试 |
| `jar` | 打包成 jar |
| `build` | 编译 + 测试 + 打包（最常用） |
| `clean` | 删除 build 目录 |

---

## 4. 依赖管理

### 4.1 依赖声明

```groovy
dependencies {
    // 格式：'groupId:artifactId:version'
    implementation 'org.springframework:spring-context:5.3.20'

    // 排除传递依赖
    implementation('org.springframework:spring-context:5.3.20') {
        exclude group: 'commons-logging', module: 'commons-logging'
    }

    // 强制指定版本（解决冲突）
    implementation('org.slf4j:slf4j-api:1.7.36') {
        force = true
    }
}
```

### 4.2 统一管理版本

```groovy
// 方式一：用 ext 定义版本变量（简单直接）
ext {
    slf4jVersion = '1.7.36'
    junitVersion = '4.13.2'
}

dependencies {
    implementation "org.slf4j:slf4j-api:${slf4jVersion}"
    testImplementation "junit:junit:${junitVersion}"
}
```

### 4.3 查看依赖树

```bash
# 查看所有依赖树
./gradlew dependencies

# 只看 runtimeClasspath（最常用，看实际运行时依赖）
./gradlew dependencies --configuration runtimeClasspath

# 子模块的依赖树
./gradlew :module-name:dependencies
```

### 4.4 配置阿里云镜像（加速下载）

```groovy
// build.gradle 或 settings.gradle
repositories {
    maven {
        url 'https://maven.aliyun.com/repository/public'
    }
    mavenCentral()
}
```

**全局配置（推荐）：** `~/.gradle/init.gradle`

```groovy
// 对所有项目生效，不用每个项目单独配置
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        mavenCentral()
    }
}
```

---

## 5. 日常常用命令

```bash
# ===== 最常用（记这几个就够了）=====
./gradlew clean build          # 清理后完整构建（编译+测试+打包）
./gradlew clean build -x test  # 跳过测试构建（-x 表示排除某个 task）
./gradlew test                 # 只运行测试

# ===== 清理 =====
./gradlew clean                # 删除 build 目录

# ===== 编译 =====
./gradlew compileJava          # 只编译主代码
./gradlew compileTestJava      # 只编译测试代码

# ===== 测试 =====
./gradlew test                             # 运行所有测试
./gradlew test --tests "com.example.*"     # 运行某个包下的所有测试
./gradlew test --tests "*.UserServiceTest" # 运行指定测试类
./gradlew test --tests "*.UserServiceTest.testRegister"  # 运行指定测试方法

# ===== 打包 =====
./gradlew jar                  # 打包成 jar（在 build/libs/ 下）
./gradlew build                # 完整构建（包含 jar）

# ===== 依赖分析 =====
./gradlew dependencies                                        # 查看完整依赖树
./gradlew dependencies --configuration runtimeClasspath       # 只看运行时依赖
./gradlew :submodule:dependencies                             # 子模块依赖树

# ===== 查看任务 =====
./gradlew tasks                # 查看所有可用任务
./gradlew tasks --all          # 包含内部任务

# ===== 调试 =====
./gradlew build --info         # 输出详细信息
./gradlew build --debug        # 输出调试级别信息（很多，一般不用）
./gradlew build --stacktrace   # 出错时打印完整堆栈（排查问题常用）

# ===== 多模块 =====
./gradlew :maven-service:build          # 只构建某个子模块
./gradlew :maven-service:test           # 只跑某个子模块的测试
./gradlew build --parallel              # 并行构建（加速）
```

---

## 6. 单元测试

### 6.1 配置

```groovy
dependencies {
    testImplementation 'junit:junit:4.13.2'
}

// 配置测试任务（通常不需要，保持默认即可）
test {
    // 测试失败时继续执行其他测试（不推荐，默认遇到失败停止）
    // ignoreFailures = true

    // 显示测试输出（默认不显示 System.out.println）
    testLogging {
        events "passed", "skipped", "failed"
    }
}
```

### 6.2 查看测试报告

测试完成后，HTML 报告在：
```
build/reports/tests/test/index.html
```

用浏览器打开，可以看到每个测试的通过/失败详情。

---

## 7. 分模块设计（多项目构建）

### 7.1 目录结构

```
my-project/
├── settings.gradle      # 声明所有子模块（类似 Maven 父 pom 的 <modules>）
├── build.gradle         # 根项目配置（公共配置放这里）
├── gradle-common/
│   └── build.gradle
├── gradle-domain/
│   └── build.gradle
├── gradle-service/
│   └── build.gradle
└── gradle-web/
    └── build.gradle
```

### 7.2 settings.gradle（声明子模块）

```groovy
// 项目名称
rootProject.name = 'my-project'

// 声明所有子模块（等同于 Maven 父 pom 的 <modules>）
include 'gradle-common'
include 'gradle-domain'
include 'gradle-service'
include 'gradle-web'
```

### 7.3 根 build.gradle（公共配置）

```groovy
// allprojects：对所有项目（含根项目）生效
allprojects {
    group = 'com.example'
    version = '1.0-SNAPSHOT'
}

// subprojects：只对子模块生效（不含根项目）
subprojects {
    apply plugin: 'java'

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        mavenCentral()
    }

    // 所有子模块都有 junit（等同于 Maven 父 pom 的 <dependencies>）
    dependencies {
        testImplementation 'junit:junit:4.13.2'
    }
}
```

### 7.4 子模块 build.gradle（模块间依赖）

```groovy
// gradle-service/build.gradle
dependencies {
    // 引用同项目的子模块，用 project(':模块名')
    implementation project(':gradle-domain')
    implementation project(':gradle-common')

    runtimeOnly 'mysql:mysql-connector-java:8.0.30'
}
```

---

## 8. 常见问题排查

### 问题 1：Gradle 同步很慢或卡住

**原因：** 下载 Gradle Wrapper 本身或依赖时网络慢。

**解决步骤：**

步骤 1：配置全局镜像（`~/.gradle/init.gradle`）
```groovy
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        mavenCentral()
    }
}
```

步骤 2：如果是 Wrapper 下载慢，手动下载后放到本地缓存
```
~/.gradle/wrapper/dists/gradle-x.x.x-bin/
```

---

### 问题 2：Could not resolve / Could not download（依赖下载失败）

**解决步骤：**

步骤 1：确认网络和镜像配置正确

步骤 2：清除 Gradle 缓存后重新同步
```bash
# 删除缓存（类似 Maven 删 .lastUpdated 文件）
rm -rf ~/.gradle/caches

# 重新构建
./gradlew clean build --refresh-dependencies
```

步骤 3：检查依赖版本是否存在（去 Maven Central 搜索确认）

---

### 问题 3：Build Cache 导致改了代码但没生效

**原因：** Gradle 增量构建认为没有变化，跳过了编译。

**解决：**
```bash
./gradlew clean build   # clean 后重新构建，强制全量编译
```

---

### 问题 4：IDEA 中代码爆红但 build.gradle 没问题

**解决步骤（按顺序尝试）：**

步骤 1：IDEA 右侧 Gradle 面板 → 点击刷新（Reload All Gradle Projects）

步骤 2：`File → Invalidate Caches → Invalidate and Restart`

步骤 3：检查 `File → Project Structure → SDKs`，确认 JDK 版本正确

---

### 问题 5：多模块中子模块找不到另一个子模块的类

**原因：** 忘记在 `settings.gradle` 中 include 该模块，或依赖写错了。

**检查步骤：**

步骤 1：确认 `settings.gradle` 中包含了被依赖的模块名

步骤 2：确认子模块 `build.gradle` 中用的是 `project(':模块名')`，不是字符串坐标

步骤 3：执行一次 `./gradlew :被依赖模块:install` 确保该模块已构建

---

### 问题 6：Gradle 和 Maven 依赖格式转换

```groovy
// Maven 格式
// <groupId>org.slf4j</groupId>
// <artifactId>slf4j-api</artifactId>
// <version>1.7.36</version>

// Gradle 格式（中间用冒号分隔）
implementation 'org.slf4j:slf4j-api:1.7.36'
```

---

## 附录：Gradle vs Maven 概念速查

| Maven | Gradle | 说明 |
|-------|--------|------|
| `pom.xml` | `build.gradle` | 项目配置文件 |
| `<modules>` | `settings.gradle` 的 `include` | 声明子模块 |
| `<parent>` | 根项目 `subprojects {}` | 公共配置继承 |
| `<dependencyManagement>` | `ext {}` 版本变量 或 `platform()` | 版本统一管理 |
| `compile` scope | `implementation` | 编译+运行依赖 |
| `test` scope | `testImplementation` | 仅测试依赖 |
| `runtime` scope | `runtimeOnly` | 仅运行时依赖 |
| `provided` scope | `compileOnly` | 仅编译依赖 |
| `mvn clean install` | `./gradlew clean build` | 常用构建命令 |
| `mvn test` | `./gradlew test` | 运行测试 |
| `mvn dependency:tree` | `./gradlew dependencies` | 查看依赖树 |
| `~/.m2/repository` | `~/.gradle/caches` | 本地缓存目录 |
