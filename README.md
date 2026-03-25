# Floyd-Core

Floyd-Core 是一款轻量级的 Minecraft 插件开发框架，基于 Spring Framework 和 PaperMC API 构建。

## 特性

- 🍃 **轻量级设计**：简洁的 API，易于上手和使用
- 🌱 **Spring 集成**：内置 Spring Framework 支持，方便依赖注入和管理
- 📦 **物品序列化**：提供 ItemStack 序列化和反序列化工具
- 📝 **日志系统**：内置控制台日志和文件日志支持
- 🔧 **可定制性**：支持自定义 Banner、配置和生命周期管理

## 环境要求

- Java 21+
- Maven 3.6+
- PaperMC 1.21.11 或更高版本

## 依赖

- Spring Framework 6.2.7
- Lombok 1.18.30
- PaperMC API 1.21.11

## 快速开始

### 1. Maven 依赖

```xml
<dependency>
    <groupId>com.codefish.mc</groupId>
    <artifactId>floyd-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 创建你的插件

继承 `FloydPlugin` 类并实现抽象方法：

```java
public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        // 插件启用时的初始化逻辑
        getLogger().info("插件已启用！");
    }
    
    @Override
    protected void cleanup() {
        // 插件禁用时的清理逻辑
        getLogger().info("插件已禁用！");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        // 返回 Spring 配置类数组
        return new Class[]{MyConfig.class};
    }
}
```

### 3. 使用 Spring 配置

```java
@Configuration
public class MyConfig {
    
    @Bean
    public MyService myService() {
        return new MyService();
    }
}
```

### 4. 使用日志系统

```java
// 获取日志实例
logger().info("这是一条信息");
logger().debug("调试信息");
logger().warning("警告信息");
logger().error("错误信息");
logger().error("异常信息", exception);
```

### 5. 物品序列化

```java
// 使用 BukkitItemStackSerializer
ItemStackSerializer serializer = new BukkitItemStackSerializer();

// 序列化
String json = serializer.serialize(itemStack);

// 反序列化
ItemStack item = serializer.deserialize(json);
```

## 核心模块

### FloydPlugin

插件主类，提供了：
- Spring ApplicationContext 集成
- 自定义 Banner 支持
- 配置文件自动保存
- 日志系统初始化
- 插件生命周期管理（onEnable/onDisable）

### 日志系统（Logging）

- `ConsoleLogger`：日志接口
- `DefaultConsoleLogger`：默认实现，支持控制台和文件双重输出

### 物品序列化（Inventory IO）

- `ItemStackSerializer`：物品序列化接口
- `BukkitItemStackSerializer`：基于 Bukkit 的实现
- `ItemStackDeserializeException`：反序列化异常
- `ItemStackSerializeException`：序列化异常

### 工具类（Util）

- `FileUtil`：文件操作工具类

### 异常处理

- `PluginBizException`：插件业务异常类

## 配置示例

在 `config.yml` 中配置日志：

```yaml
logging:
  file:
    enable: "true"  # 启用文件日志
```

## 项目结构

```
floyd-core/
├── src/main/java/com/floyd/core/
│   ├── FloydPlugin.java          # 插件基类
│   ├── PluginBizException.java   # 业务异常
│   ├── PluginConstants.java      # 常量定义
│   ├── inventory/io/             # 物品序列化模块
│   ├── logging/                  # 日志模块
│   └── util/                     # 工具类模块
└── pom.xml                       # Maven 配置
```

## 编译与打包

```bash
# 编译项目
mvn clean package

# 生成的 jar 文件位于 target/ 目录
- floyd-core-1.0.0-SNAPSHOT.jar           # 普通 jar
- floyd-core-1.0.0-SNAPSHOT-shaded.jar    # 包含依赖的 fat jar
```

## 作者

- **floyd**

## 许可证

本项目采用 Apache 2.0 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 贡献

欢迎提交 Issue 和 Pull Request！

---

**注意**：本项目目前处于开发阶段，API 可能会有变动。
