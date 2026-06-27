# maven-service — 业务逻辑模块

## 这个模块是干什么的

处理核心业务逻辑，演示两个重点概念：

**1. 模块间依赖**：显式依赖 `maven-domain` 和 `maven-common`。

**2. 依赖传递**：
- `maven-common` 的 `slf4j-api`（compile）→ 自动传递给本模块，可直接使用
- `maven-common` 的 `logback-classic`（runtime）→ 传递后仍为 runtime
- `maven-common` 的 `junit`（test）→ **不传递**，本模块必须自己声明

## 包含什么

- `UserService` — 用户业务服务（findById、register）
- `UserServiceTest` — 单元测试（正常流程 + 异常场景）

## 怎么运行/验证

```bash
# 在 maven-service 目录下执行（先确保根目录 mvn install 过一次）

# 运行所有测试
mvn test

# 只运行 UserServiceTest
mvn test -Dtest=UserServiceTest

# 查看本模块完整依赖树（验证依赖传递）
mvn dependency:tree
```

> 运行前需先在根目录执行 `mvn install -DskipTests`，确保 domain 和 common 已安装到本地仓库。
