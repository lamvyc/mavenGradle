# gradle-domain — 实体模块

## 是什么

存放业务实体类（POJO），是整个项目**最底层**的模块。

- 不依赖任何其他子模块
- 无任何业务逻辑
- 可被任意模块引用，不会引起循环依赖

## 包含什么

- `User` — 用户实体（id、username、email）

## 如何运行/调试

```bash
# 在 gradle-project 目录下执行

# 编译验证
./gradlew :gradle-domain:compileJava

# 构建（其他模块引用它之前，需先构建）
./gradlew :gradle-domain:build
```
