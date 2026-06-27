# Maven 本机安装与配置

## 环境信息

- 系统：macOS（Apple Silicon，Homebrew 在 `/opt/homebrew`）
- Java：已安装（OpenJDK 25）

---

## 第一步：安装 Maven

从 Apache 官方下载二进制包（不依赖 Homebrew，境内下载更稳定）：

```bash
# 下载
curl -L "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz" -o /tmp/maven.tar.gz

# 解压到 /opt/homebrew
tar -xzf /tmp/maven.tar.gz -C /opt/homebrew
mv /opt/homebrew/apache-maven-3.9.6 /opt/homebrew/maven

# 清理
rm /tmp/maven.tar.gz
```

---

## 第二步：配置 PATH

```bash
echo 'export PATH="/opt/homebrew/maven/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

验证：
```bash
mvn -version
# 输出：Apache Maven 3.9.6，Java version: 25.0.2
```

---

## 第三步：配置阿里云镜像（加速依赖下载）

Maven 默认从国外中央仓库下载，速度慢。配置阿里云镜像解决。

**步骤 1：** 找到 settings.xml 位置

```bash
# Maven 安装目录下的默认配置（不要改这个）
/opt/homebrew/Cellar/maven/3.9.x/libexec/conf/settings.xml

# 用户级配置（推荐改这个，只影响当前用户）
~/.m2/settings.xml
```

**步骤 2：** 创建用户级 settings.xml

```bash
mkdir -p ~/.m2
```

新建 `~/.m2/settings.xml`，写入以下内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
```

**步骤 3：** 验证镜像生效

```bash
cd /Users/unravel/mavenGradle/maven-project
mvn clean install
```

下载依赖时看到 `aliyun` 字样说明镜像生效。

---

## 第四步：配置 IDEA 使用本机 Maven

1. 打开 IDEA → `File → Settings → Build, Execution, Deployment → Build Tools → Maven`
2. `Maven home path` 改为：`/opt/homebrew/Cellar/maven/3.9.x/libexec`
   （实际版本号用 `ls /opt/homebrew/Cellar/maven/` 查看）
3. `User settings file` 改为：`/Users/你的用户名/.m2/settings.xml`
4. 点击 `Apply` → `OK`
5. Maven 面板点刷新按钮重新同步

---

## 常见问题

### 问题 1：`mvn` 在 IDEA 内置终端中找不到（command not found）

**原因：** IDEA 内置终端默认不是登录 shell，不加载 `~/.zshrc`，PATH 不生效。

**解决步骤：**

步骤 1：删除 pyenv 锁文件（如果有 pyenv 且报锁文件错误）
```bash
rm -f ~/.pyenv/shims/.pyenv-shim
```

步骤 2：重新加载配置
```bash
source ~/.zshrc
mvn -version
```

步骤 3：如果还不行，改 IDEA Terminal 使用登录 shell（一劳永逸）

`Settings → Tools → Terminal → Shell path` 改为：`/bin/zsh -l`

关掉当前 Terminal 标签，重新打开即可。

---

### 问题 2：首次构建报 `No route to host`（插件下载失败）

**原因：** 没有配置阿里云镜像，Maven 直连国外仓库失败。

**解决：** 先完成第三步的阿里云镜像配置，再重新构建。

---

### 问题 3：`mvn -version` 有 WARNING 输出

```
WARNING: A restricted method in java.lang.System has been called
```

**原因：** Java 25 对 Maven 3.9.x 使用的旧 API 发出警告，不影响功能，忽略即可。

---

## 常用验证命令

```bash
# 查看 Maven 版本和 Java 版本
mvn -version

# 查看生效的 settings.xml 配置
mvn help:effective-settings

# 在 maven-project 目录构建
cd /Users/unravel/mavenGradle/maven-project
mvn clean install

# 跳过测试构建（更快）
mvn clean install -DskipTests

# 运行所有测试
mvn test
```
