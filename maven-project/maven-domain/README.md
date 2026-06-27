# maven-domain — 实体模块

## 这个模块是干什么的

存放业务实体类（POJO），是整个项目**最底层**的模块，不依赖任何其他子模块，也没有任何业务逻辑。

这样设计的好处：domain 可以被任意模块引用，不会引入循环依赖。

## 包含什么

- `User` — 用户实体（id、username、email）

## 怎么运行/验证

```bash
# 在 maven-project/maven-domain 目录下执行

# 编译（验证实体类无语法错误）
mvn compile

# 安装到本地仓库（其他模块才能引用它）
mvn install
```
