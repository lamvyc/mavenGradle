# gradle-web — 入口模块

## 是什么

整个项目的启动入口，最顶层的模块。演示：

- 依赖 gradle-service，通过依赖传递间接使用 domain 和 common
- fat jar 打包（将所有依赖打进一个 jar，直接用 `java -jar` 运行）
- 依赖排除写法（见 `build.gradle` 注释）

## 包含什么

- `Application` — main 方法，演示完整调用链

## 如何运行/调试

```bash
# 在 gradle-project 目录下执行

# 步骤 1：构建整个项目（含打包）
./gradlew clean build

# 步骤 2：运行
java -jar gradle-web/build/libs/gradle-web-1.0-SNAPSHOT.jar
```

预期输出：
```
应用启动...
查询用户，id=1
查询到用户：User{id=1, username='alice', email='alice@example.com'}
注册新用户：username=charlie
注册成功：User{id=..., username='charlie', email='charlie@example.com'}
```

## 调试技巧

```bash
# 构建失败时，加 --stacktrace 查看详细错误
./gradlew build --stacktrace

# 查看 web 模块的实际运行时依赖（确认 fat jar 包含了哪些依赖）
./gradlew :gradle-web:dependencies --configuration runtimeClasspath
```
