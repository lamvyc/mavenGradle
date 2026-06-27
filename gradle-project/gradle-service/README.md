# gradle-service — 业务逻辑模块

## 是什么

处理核心业务逻辑，演示两个重点：

**1. 模块间依赖**：通过 `project(':gradle-domain')` 和 `project(':gradle-common')` 引用兄弟模块。

**2. implementation vs api 的区别**：
- 本模块用 `implementation` 依赖 domain 和 common
- 结果：gradle-web 依赖本模块后，**看不到** domain 和 common 的类（需自己声明）
- 若改成 `api`，gradle-web 无需声明也能直接用 domain/common 的类

## 包含什么

- `UserService` — findById、register 业务方法
- `UserServiceTest` — 正常流程 + 异常场景的单元测试

## 如何运行/调试

```bash
# 在 gradle-project 目录下执行

# 运行所有测试
./gradlew :gradle-service:test

# 运行指定测试类
./gradlew :gradle-service:test --tests "*.UserServiceTest"

# 运行指定测试方法
./gradlew :gradle-service:test --tests "*.UserServiceTest.testRegister_success"

# 查看本模块的完整依赖树（验证依赖传递）
./gradlew :gradle-service:dependencies --configuration runtimeClasspath

# 查看测试报告
open gradle-service/build/reports/tests/test/index.html
```
