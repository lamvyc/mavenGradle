# maven-web — 入口模块

## 这个模块是干什么的

整个项目的启动入口，是**最顶层**的模块。演示：

- 通过依赖 `maven-service`，间接获得完整依赖链（web → service → domain, common）
- 依赖排除（`<exclusion>`）的写法——见 `pom.xml` 注释
- 打包为可执行 jar（maven-jar-plugin 配置 mainClass）

## 包含什么

- `Application` — main 方法，调用 UserService 演示完整调用链

## 怎么运行/验证

```bash
# 步骤 1：在 maven-project 根目录构建整个项目
cd /Users/unravel/mavenGradle/maven-project
mvn clean install

# 步骤 2：运行可执行 jar
java -jar maven-web/target/maven-web-1.0-SNAPSHOT.jar
```

预期输出：
```
应用启动...
查询用户，id=1
查询到用户：User{id=1, username='alice', email='alice@example.com'}
注册新用户：username=bob
注册成功：User{id=..., username='bob', email='bob@example.com'}
```
