# 核心模块文档

Floyd-Core 由多个功能模块组成，每个模块都专注于解决特定的开发需求。本文档详细介绍各核心模块的功能和使用方法。

## 目录

- [FloydPlugin 基类](#floydplugin-基类)
- [日志模块](#日志模块)
- [权限模块](#权限模块)
- [物品序列化模块](#物品序列化模块)
- [命令系统模块](#命令系统模块)
- [数据库模块](#数据库模块)
- [国际化模块](#国际化模块)
- [配置管理模块](#配置管理模块)
- [工具类模块](#工具类模块)

---

## FloydPlugin 基类

`FloydPlugin` 是所有插件的主类基类，继承自 PaperMC 的 `JavaPlugin`，提供了框架的核心功能。

### 主要功能

1. **Spring 容器集成** - 自动初始化和管 Spring ApplicationContext
2. **生命周期管理** - 统一的插件启用/禁用流程
3. **配置管理** - 自动保存默认配置文件
4. **日志系统** - 提供便捷的日志访问方法
5. **Banner 支持** - 自定义启动横幅

### 抽象方法

```java
public abstract class MyPlugin extends FloydPlugin {
    
    /**
     * 返回插件名称
     */
    @Override
    public abstract String getPluginName();
    
    /**
     * 插件启用时的初始化逻辑
     * 在 Spring 容器初始化后调用
     */
    @Override
    protected abstract void initialize();
    
    /**
     * 插件禁用时的清理逻辑
     */
    @Override
    protected abstract void cleanup();
    
    /**
     * 返回 Spring 配置类数组
     */
    @Override
    protected abstract Class<?>[] getConfigClasses();
}
```

### 可用方法

```java
// 获取日志记录器
logger().info("信息消息");
logger().error("错误消息", exception);

// 获取 Spring ApplicationContext
getApplicationContext();

// 获取 Bean 实例
MyService service = getBean(MyService.class);

// 获取插件数据文件夹
getDataFolder();

// 获取配置文件
getConfig();
```

### 使用示例

```java
public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        logger().info("插件初始化完成");
        
        // 获取服务实例
        PlayerService playerService = getBean(PlayerService.class);
        playerService.init();
    }
    
    @Override
    protected void cleanup() {
        logger().info("正在清理资源...");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class[]{AppConfig.class};
    }
}
```

---

## 日志模块

日志模块提供多级别、多输出的日志记录功能。

### 核心类

- `ConsoleLogger` - 日志接口
- `DefaultConsoleLogger` - 默认实现
- `LogLevel` - 日志级别枚举

### 日志级别

| 级别 | 用途 | 方法 |
|------|------|------|
| DEBUG | 调试信息 | `logger().debug()` |
| INFO | 一般信息 | `logger().info()` |
| WARN | 警告信息 | `logger().warning()` |
| ERROR | 错误信息 | `logger().error()` |

### 基本用法

```java
// 简单日志
logger().info("插件已启动");
logger().debug("调试信息: " + variable);
logger().warning("警告: 配置文件不存在");
logger().error("发生错误");

// 带异常的日志
try {
    // 可能抛出异常的代码
} catch (Exception e) {
    logger().error("处理失败", e);
}
```

### 文件日志配置

在 `config.yml` 中配置：

```yaml
logging:
  file:
    enable: true              # 启用文件日志
    filename: plugin.log      # 日志文件名
    max-size: 10MB            # 单个日志文件最大大小
    max-history: 7            # 保留天数
```

### 日志输出位置

- **控制台**: 所有级别的日志
- **文件**: `{dataFolder}/plugin.log`（启用时）

---

## 权限模块

基于 AOP 的权限检查系统，通过注解实现无侵入式权限验证。

### 核心组件

- `@RequiredPermission` - 权限注解
- `PermissionAspect` - 权限切面
- `PermissionUtil` - 权限工具类

### 注解参数

```java
@RequiredPermission(
    value = "permission.node",      // 权限节点（必填）
    message = "自定义错误消息",      // 错误消息（可选）
    tipPermValue = false            // 是否显示权限值（可选）
)
```

### 使用示例

```java
@Service
public class BackpackService {
    
    // 基础权限检查
    @RequiredPermission("myplugin.backpack.open")
    public void openBackpack(Player player, int id) {
        // 只有拥有权限的玩家才能执行
    }
    
    // 自定义错误消息
    @RequiredPermission(
        value = "myplugin.backpack.upgrade",
        message = "§c你需要升级权限才能执行此操作!"
    )
    public void upgradeBackpack(Player player) {
        // 业务逻辑
    }
    
    // 显示权限值
    @RequiredPermission(
        value = "myplugin.admin.delete",
        tipPermValue = true
    )
    public void deleteItem(Player player, String itemId) {
        // 权限不足时显示: "你没有权限! 需要: myplugin.admin.delete"
    }
}
```

### 注意事项

1. 方法必须包含 `Player` 或 `CommandSender` 参数
2. 权限检查在方法执行前进行
3. 权限不足时方法不会执行，直接返回
4. 错误消息会自动发送给玩家

---

## 物品序列化模块

提供 ItemStack 的序列化和反序列化功能，便于存储和传输。

### 核心类

- `ItemStackSerializer` - 序列化接口
- `BukkitItemStackSerializer` - Bukkit 实现
- `ItemStackSerializeException` - 序列化异常
- `ItemStackDeserializeException` - 反序列化异常

### 基本用法

```java
import com.floyd.core.inventory.ItemStackSerializer;
import com.floyd.core.inventory.BukkitItemStackSerializer;

// 创建序列化器
ItemStackSerializer serializer = new BukkitItemStackSerializer();

        // 序列化 ItemStack -> String
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        String serialized = serializer.serialize(item);

        // 反序列化 String -> ItemStack
        ItemStack restored = serializer.deserialize(serialized);
```

### 存储到配置文件

```java
// 保存物品到 config.yml
ItemStack item = player.getInventory().getItemInMainHand();
String serialized = serializer.serialize(item);
getConfig().set("saved-item", serialized);
saveConfig();

// 从 config.yml 读取物品
String saved = getConfig().getString("saved-item");
ItemStack loaded = serializer.deserialize(saved);
```

### 存储到数据库

```java
// 保存到数据库
String itemData = serializer.serialize(itemStack);
database.execute("INSERT INTO items (player, item_data) VALUES (?, ?)", 
                 playerName, itemData);

// 从数据库读取
String itemData = database.query("SELECT item_data FROM items WHERE player = ?", 
                                 playerName);
ItemStack item = serializer.deserialize(itemData);
```

### 异常处理

```java
try {
    ItemStack item = serializer.deserialize(data);
} catch (ItemStackDeserializeException e) {
    logger().error("物品数据损坏", e);
    // 处理异常情况
}
```

---

## 命令系统模块

强大的命令处理系统，支持子命令、参数补全和权限检查。

### 核心组件

- `CommandDispatcher` - 命令分发器
- `CommandHandler` - 命令处理器
- `SubCommandHandler` - 子命令处理器
- `TrieCommandCompleter` - Trie 树命令补全

### 详细文档

请参阅 [命令系统文档](command-system.md) 获取完整的使用指南。

### 快速示例

```java
@Component
public class MyCommandHandler {
    
    @CommandHandler("myplugin")
    public void handleMain(CommandSender sender, String[] args) {
        sender.sendMessage("主命令");
    }
    
    @SubCommandHandler(parent = "myplugin", name = "reload")
    @RequiredPermission("myplugin.admin")
    public void handleReload(Player player, String[] args) {
        player.sendMessage("配置已重载");
    }
}
```

---

## 数据库模块

内置数据库管理模块，支持多种数据库类型和 SQL 语法构建。

### 核心组件

- `DatabaseManager` - 数据库管理器
- `DatabaseType` - 数据库类型枚举
- `Syntax` - SQL 语法构建器
- `Backup` - 数据备份功能

### 支持的数据库

- SQLite
- MySQL
- MariaDB
- H2

### 详细文档

请参阅 [数据库模块文档](database-module.md) 获取完整的使用指南。

### 快速示例

```java
@Autowired
private DatabaseManager dbManager;

// 创建表
dbManager.execute(
    Syntax.create()
        .table("players")
        .column("uuid", "TEXT PRIMARY KEY")
        .column("name", "TEXT")
        .column("balance", "INTEGER DEFAULT 0")
);

// 插入数据
dbManager.execute(
    Syntax.insert()
        .into("players")
        .values(uuid, name, balance)
);

// 查询数据
ResultSet rs = dbManager.query(
    Syntax.select()
        .from("players")
        .where("uuid = ?", uuid)
);
```

---

## 国际化模块

多语言支持模块，提供消息管理和动态语言切换功能。

### 核心组件

- `I18nMessageProvider` - 消息提供者
- `I18nSettingManager` - 语言设置管理器
- `LocaleMessage` - 区域消息

### 详细文档

请参阅 [国际化模块文档](i18n-module.md) 获取完整的使用指南。

### 快速示例

```java
@Autowired
private I18nMessageProvider i18nProvider;

// 获取消息
String message = i18nProvider.getMessage(player, "welcome.message");

// 带占位符的消息
String formatted = i18nProvider.getMessage(player, "greeting", 
                                           player.getName());

// 语言文件 (zh_cn.yml)
welcome:
  message: "欢迎加入服务器!"
greeting:
  message: "你好, {0}!"
```

---

## 配置管理模块

类型安全的配置管理系统，支持热重载和默认值管理。

### 核心组件

- `PluginSettingsManager` - 设置管理器
- `PluginSettingsHolder` - 设置持有者
- `SettingsReloadAware` - 重载监听接口

### 详细文档

请参阅 [配置管理文档](configuration.md) 获取完整的使用指南。

### 快速示例

```java
@Component
public class PluginSettings implements SettingsReloadAware {
    
    private boolean featureEnabled;
    private int maxItems;
    
    @Override
    public void reload(ConfigurationSection config) {
        this.featureEnabled = config.getBoolean("feature.enabled", true);
        this.maxItems = config.getInt("feature.max-items", 64);
    }
    
    public boolean isFeatureEnabled() {
        return featureEnabled;
    }
}
```

---

## 工具类模块

常用的工具类集合，简化日常开发任务。

### FileUtil - 文件工具

```java
// 读取文件内容
String content = FileUtil.readFile(file, StandardCharsets.UTF_8);

// 写入文件
FileUtil.writeFile(file, content, StandardCharsets.UTF_8);

// 复制文件
FileUtil.copyFile(source, target);

// 删除目录
FileUtil.deleteDirectory(directory);
```

### StrUtil - 字符串工具

```java
// 判断字符串是否为空
boolean empty = StrUtil.isEmpty(str);
boolean blank = StrUtil.isBlank(str);

// 格式化字符串
String formatted = StrUtil.format("Hello, {}", name);

// 截断字符串
String truncated = StrUtil.truncate(text, 100, "...");

// 颜色代码转换
String colored = StrUtil.translateColorCodes("&6Hello &eWorld");
```

### DateUtil - 日期工具

```java
// 格式化日期
String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

// 解析日期
Date date = DateUtil.parse("2024-01-01", "yyyy-MM-dd");

// 获取相对时间
String relative = DateUtil.getRelativeTime(timestamp);
// 输出: "2小时前", "3天前" 等
```

---

## 模块依赖关系

```
FloydPlugin (核心)
    ├── Logging Module (日志)
    ├── Permission Module (权限) ← 依赖 Logging
    ├── Command Module (命令) ← 依赖 Permission
    ├── Database Module (数据库) ← 依赖 Logging
    ├── I18n Module (国际化) ← 依赖 Settings
    ├── Settings Module (配置) ← 依赖 Logging
    └── Inventory IO (物品序列化)
```

## 最佳实践

### 1. 模块化设计

将不同功能划分到不同的模块和服务中：

```java
@Service
public class PlayerDataService {
    @Autowired private DatabaseManager db;
    @Autowired private I18nMessageProvider i18n;
}

@Service
public class ItemService {
    @Autowired private ItemStackSerializer serializer;
}
```

### 2. 合理使用日志

```java
// ✅ 好的做法
logger().debug("加载玩家数据: {}", playerId);
logger().info("插件初始化完成");
logger().error("数据库连接失败", exception);

// ❌ 避免的做法
System.out.println("调试信息");  // 使用 logger
e.printStackTrace();             // 使用 logger.error()
```

### 3. 异常处理

```java
// ✅ 正确的异常处理
try {
    ItemStack item = serializer.deserialize(data);
} catch (ItemStackDeserializeException e) {
    logger().error("物品数据损坏: {}", data, e);
    return null;  // 或抛出业务异常
}

// ❌ 避免吞掉异常
try {
    // ...
} catch (Exception e) {
    // 什么都不做
}
```

### 4. 资源管理

```java
@Override
protected void cleanup() {
    // 关闭数据库连接
    dbManager.close();
    
    // 保存缓存数据
    cacheService.saveAll();
    
    // 清理临时文件
    tempService.cleanup();
}
```

---

## 总结

Floyd-Core 的各个模块协同工作，为插件开发提供了完整的技术栈：

- **FloydPlugin**: 统一的生命周期管理
- **日志模块**: 完善的日志记录
- **权限模块**: 便捷的权限控制
- **物品序列化**: 安全的物品存储
- **命令系统**: 灵活的命令处理
- **数据库模块**: 可靠的数据持久化
- **国际化模块**: 多语言支持
- **配置管理**: 类型安全的配置
- **工具类**: 常用功能封装

合理利用这些模块，可以大幅提高开发效率和代码质量。