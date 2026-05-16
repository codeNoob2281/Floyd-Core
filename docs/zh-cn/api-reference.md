# API 参考文档

本文档提供 Floyd-Core 框架的完整 API 参考，包括核心类、接口和方法说明。

## 目录

- [核心类](#核心类)
- [命令系统](#命令系统)
- [数据库模块](#数据库模块)
- [国际化模块](#国际化模块)
- [权限系统](#权限系统)
- [配置管理](#配置管理)
- [日志系统](#日志系统)
- [物品序列化](#物品序列化)
- [工具类](#工具类)

---

## 核心类

### FloydPlugin

插件基类，所有插件的主类都应继承此类。

**位置**: `com.floyd.core.FloydPlugin`

**主要方法**:

```java
// 获取插件名称
public abstract String getPluginName();

// 初始化逻辑（插件启用时调用）
protected abstract void initialize();

// 清理逻辑（插件禁用时调用）
protected abstract void cleanup();

// 返回 Spring 配置类
protected abstract Class<?>[] getConfigClasses();

// 获取日志记录器
protected ConsoleLogger logger();

// 获取 Spring ApplicationContext
public ApplicationContext getApplicationContext();

// 获取 Bean 实例
public <T> T getBean(Class<T> clazz);

// 获取插件实例
public static FloydPlugin getInstance();

// 获取数据文件夹
public File getDataFolder();

// 获取配置文件
public FileConfiguration getConfig();

// 保存配置文件
public void saveConfig();

// 重新加载配置文件
public void reloadConfig();

// 保存默认配置
public void saveDefaultConfig();

// 保存资源文件
public void saveResource(String resourcePath, boolean replace);
```

**使用示例**:

```java
public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        logger().info("插件已启用");
    }
    
    @Override
    protected void cleanup() {
        logger().info("插件已禁用");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class[]{AppConfig.class};
    }
}
```

---

## 命令系统

### CommandHandler

命令处理器注解。

**位置**: `com.floyd.core.command.CommandHandler`

**属性**:
- `value`: 命令名称（必填）
- `aliases`: 命令别名（可选）

**使用示例**:

```java
@CommandHandler("myplugin")
public void handleMain(CommandSender sender, String[] args) {
    // 处理命令
}
```

### SubCommandHandler

子命令处理器注解。

**位置**: `com.floyd.core.command.SubCommandHandler`

**属性**:
- `parent`: 父命令名称（必填）
- `name`: 子命令名称（必填）

**使用示例**:

```java
@SubCommandHandler(parent = "myplugin", name = "reload")
public void handleReload(Player player, String[] args) {
    // 处理子命令
}
```

### CommandCompleter

命令补全器接口。

**位置**: `com.floyd.core.command.CommandCompleter`

**方法**:

```java
// 获取补全列表
List<String> complete(CommandSender sender, String[] args);
```

**使用示例**:

```java
@Component
public class MyCompleter implements CommandCompleter {
    
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "help", "reload");
        }
        return Collections.emptyList();
    }
}
```

### TrieCommandCompleter

基于 Trie 树的命令补全器。

**位置**: `com.floyd.core.command.TrieCommandCompleter`

**主要方法**:

```java
// 添加命令路径
public void addCommandPath(String... path);

// 获取补全列表
@Override
public List<String> complete(CommandSender sender, String[] args);
```

---

## 数据库模块

### DatabaseManager

数据库管理器。

**位置**: `com.floyd.core.database.DatabaseManager`

**主要方法**:

```java
// 初始化数据库
public void initialize();

// 执行更新操作
public int execute(String sql, Object... params);

// 执行查询操作
public ResultSet query(String sql, Object... params);

// 批量执行
public int[] executeBatch(String sql, List<Object[]> batchParams);

// 获取连接
public Connection getConnection() throws SQLException;

// 关闭数据库
public void close();
```

**使用示例**:

```java
@Autowired
private DatabaseManager dbManager;

// 执行更新
dbManager.execute(
    "INSERT INTO players (uuid, name) VALUES (?, ?)",
    uuid, name
);

// 执行查询
ResultSet rs = dbManager.query(
    "SELECT * FROM players WHERE uuid = ?",
    uuid
);
```

### Syntax

SQL 语法构建器。

**位置**: `com.floyd.core.database.syntax.Syntax`

**主要方法**:

```java
// CREATE TABLE
public static CreateBuilder create();

// INSERT
public static InsertBuilder insert();

// SELECT
public static SelectBuilder select();

// UPDATE
public static UpdateBuilder update();

// DELETE
public static DeleteBuilder delete();

// ALTER TABLE
public static AlterBuilder alter();
```

**使用示例**:

```java
// 创建表
String sql = Syntax.create()
    .table("players")
    .column("uuid", "TEXT PRIMARY KEY")
    .column("name", "TEXT")
    .build();

// 插入数据
String sql = Syntax.insert()
    .into("players")
    .columns("uuid", "name")
    .values(uuid, name)
    .build();

// 查询数据
String sql = Syntax.select()
    .from("players")
    .where("uuid = ?", uuid)
    .build();
```

### DatabaseType

数据库类型枚举。

**位置**: `com.floyd.core.database.DatabaseType`

**值**:
- `SQLITE`
- `MYSQL`
- `MARIADB`
- `H2`

---

## 国际化模块

### I18nMessageProvider

消息提供者接口。

**位置**: `com.floyd.core.i18n.I18nMessageProvider`

**主要方法**:

```java
// 获取玩家语言的消息
String getMessage(Player player, String key, Object... args);

// 获取指定语言的消息
String getMessage(String locale, String key, Object... args);

// 发送消息给玩家
void sendMessage(Player player, String key, Object... args);

// 检查消息是否存在
boolean hasMessage(Player player, String key);

// 检查语言是否支持
boolean isSupported(String locale);

// 获取支持的语言列表
List<String> getSupportedLocales();

// 获取默认语言
String getDefaultLocale();

// 重载所有语言
void reloadAllLocales();
```

**使用示例**:

```java
@Autowired
private I18nMessageProvider i18nProvider;

// 获取消息
String msg = i18nProvider.getMessage(player, "welcome.message", playerName);

// 发送消息
i18nProvider.sendMessage(player, "common.success");
```

### I18nSettingManager

语言设置管理器。

**位置**: `com.floyd.core.i18n.I18nSettingManager`

**主要方法**:

```java
// 设置玩家语言
void setPlayerLocale(UUID playerId, String locale);

// 获取玩家语言
String getPlayerLocale(UUID playerId);

// 重置玩家语言
void resetPlayerLocale(UUID playerId);

// 设置默认语言
void setDefaultLocale(String locale);

// 获取默认语言
String getDefaultLocale();

// 加载语言文件
void loadLanguageFiles();

// 是否启用自动检测
boolean isAutoDetectEnabled();
```

---

## 权限系统

### @RequiredPermission

权限检查注解。

**位置**: `com.floyd.core.permission.RequiredPermission`

**属性**:

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| value | String | 是 | - | 权限节点 |
| message | String | 否 | "you don't have permission..." | 错误消息 |
| tipPermValue | boolean | 否 | false | 是否显示权限值 |

**使用示例**:

```java
@RequiredPermission("myplugin.admin.reload")
public void reloadConfig(Player player) {
    // 需要权限才能执行
}

@RequiredPermission(
    value = "myplugin.admin.kick",
    message = "§c你没有踢人权限！",
    tipPermValue = true
)
public void kickPlayer(Player admin, Player target) {
    // 自定义错误消息
}
```

### PermissionUtil

权限工具类。

**位置**: `com.floyd.core.permission.PermissionUtil`

**主要方法**:

```java
// 检查权限
public boolean hasPermission(Player player, String permission);

// 检查是否有任意一个权限
public boolean hasAnyPermission(Player player, String... permissions);

// 检查是否有所有权限
public boolean hasAllPermissions(Player player, String... permissions);
```

---

## 配置管理

### PluginSettingsManager

设置管理器。

**位置**: `com.floyd.core.settings.PluginSettingsManager`

**主要方法**:

```java
// 重载所有配置
public void reloadAll();

// 注册设置持有者
public void registerHolder(PluginSettingsHolder holder);

// 获取设置持有者
public <T extends PluginSettingsHolder> T getHolder(Class<T> clazz);
```

### SettingsReloadAware

配置重载监听接口。

**位置**: `com.floyd.core.settings.SettingsReloadAware`

**方法**:

```java
// 配置重载时调用
void reload(ConfigurationSection config);
```

**使用示例**:

```java
@Component
public class MySettings implements SettingsReloadAware {
    
    private boolean featureEnabled;
    
    @Override
    public void reload(ConfigurationSection config) {
        this.featureEnabled = config.getBoolean("feature.enabled", true);
    }
    
    public boolean isFeatureEnabled() {
        return featureEnabled;
    }
}
```

---

## 日志系统

### ConsoleLogger

日志接口。

**位置**: `com.floyd.core.logging.ConsoleLogger`

**主要方法**:

```java
// DEBUG 级别
void debug(String message);
void debug(String message, Throwable t);

// INFO 级别
void info(String message);
void info(String message, Throwable t);

// WARNING 级别
void warning(String message);
void warning(String message, Throwable t);

// ERROR 级别
void error(String message);
void error(String message, Throwable t);
```

**使用示例**:

```java
logger().info("插件已启用");
logger().debug("调试信息: {}", variable);
logger().error("发生错误", exception);
```

### LogLevel

日志级别枚举。

**位置**: `com.floyd.core.logging.LogLevel`

**值**:
- `DEBUG`
- `INFO`
- `WARNING`
- `ERROR`

---

## 物品序列化

### ItemStackSerializer

物品序列化接口。

**位置**: `com.floyd.core.inventory.io.ItemStackSerializer`

**主要方法**:

```java
// 序列化物品
String serialize(ItemStack item) throws ItemStackSerializeException;

// 反序列化物品
ItemStack deserialize(String data) throws ItemStackDeserializeException;
```

### BukkitItemStackSerializer

Bukkit 物品序列化实现。

**位置**: `com.floyd.core.inventory.io.BukkitItemStackSerializer`

**使用示例**:

```java
ItemStackSerializer serializer = new BukkitItemStackSerializer();

// 序列化
String data = serializer.serialize(itemStack);

// 反序列化
ItemStack item = serializer.deserialize(data);
```

---

## 工具类

### FileUtil

文件工具类。

**位置**: `com.floyd.core.util.FileUtil`

**主要方法**:

```java
// 读取文件
public static String readFile(File file, Charset charset) throws IOException;

// 写入文件
public static void writeFile(File file, String content, Charset charset) throws IOException;

// 复制文件
public static void copyFile(File source, File target) throws IOException;

// 删除目录
public static void deleteDirectory(File directory) throws IOException;

// 创建目录
public static void createDirectories(File directory) throws IOException;
```

**使用示例**:

```java
// 读取文件
String content = FileUtil.readFile(file, StandardCharsets.UTF_8);

// 写入文件
FileUtil.writeFile(file, content, StandardCharsets.UTF_8);
```

### StrUtil

字符串工具类。

**位置**: `com.floyd.core.util.StrUtil`

**主要方法**:

```java
// 判断是否为空
public static boolean isEmpty(String str);
public static boolean isBlank(String str);

// 格式化字符串
public static String format(String template, Object... args);

// 截断字符串
public static String truncate(String str, int maxLength, String suffix);

// 转换颜色代码
public static String translateColorCodes(String text);

// 首字母大写
public static String capitalize(String str);
```

**使用示例**:

```java
// 检查空字符串
if (StrUtil.isEmpty(str)) {
    // 处理空字符串
}

// 格式化
String msg = StrUtil.format("你好, {}", name);

// 颜色代码
String colored = StrUtil.translateColorCodes("&6Hello &eWorld");
```

### DateUtil

日期工具类。

**位置**: `com.floyd.core.util.DateUtil`

**主要方法**:

```java
// 格式化日期
public static String format(Date date, String pattern);

// 解析日期
public static Date parse(String dateString, String pattern) throws ParseException;

// 获取相对时间
public static String getRelativeTime(long timestamp);

// 获取当前时间戳
public static long currentTimestamp();
```

**使用示例**:

```java
// 格式化日期
String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

// 相对时间
String relative = DateUtil.getRelativeTime(timestamp);
// 输出: "2小时前", "3天前"
```

---

## 异常类

### PluginBizException

插件业务异常。

**位置**: `com.floyd.core.PluginBizException`

**构造方法**:

```java
// 简单异常
public PluginBizException(String message);

// 带原因的异常
public PluginBizException(String message, Throwable cause);
```

**使用示例**:

```java
if (balance < amount) {
    throw new PluginBizException("余额不足");
}
```

### ItemStackSerializeException

物品序列化异常。

**位置**: `com.floyd.core.inventory.io.ItemStackSerializeException`

### ItemStackDeserializeException

物品反序列化异常。

**位置**: `com.floyd.core.inventory.io.ItemStackDeserializeException`

---

## 常量类

### PluginConstants

插件常量。

**位置**: `com.floyd.core.PluginConstants`

**常量**:

```java
// 作者
public static final String AUTHOR = "floyd";

// 版本
public static final String VERSION = "1.0.0-SNAPSHOT";
```

---

## Spring 配置

### SpringConfig

Spring 配置类。

**位置**: `com.floyd.core.SpringConfig`

**主要 Bean**:

```java
@Bean
public CommandDispatcher commandDispatcher() {
    return new CommandDispatcher();
}

@Bean
public PermissionAspect permissionAspect() {
    return new PermissionAspect();
}
```

---

## 数据类型

### Field 系列类

数据库字段类型。

**位置**: `com.floyd.core.database.fields`

**类列表**:
- `Field` - 基础字段接口
- `FieldString` - 字符串字段
- `FieldInteger` - 整数字段
- `FieldLong` - 长整型字段
- `FieldFloat` - 浮点字段
- `FieldBoolean` - 布尔字段
- `FieldTimestamp` - 时间戳字段

**使用示例**:

```java
FieldString nameField = new FieldString("name", "Steve");
FieldInteger ageField = new FieldInteger("age", 25);
FieldTimestamp timeField = new FieldTimestamp("created_at", new Date());
```

---

## 集合类

### Trie

Trie 树数据结构。

**位置**: `com.floyd.core.collection.Trie`

**主要方法**:

```java
// 插入单词
public void insert(String word);

// 搜索单词
public boolean search(String word);

// 查找前缀
public boolean startsWith(String prefix);

// 获取所有匹配
public List<String> getAllMatches(String prefix);
```

**使用示例**:

```java
Trie trie = new Trie();
trie.insert("teleport");
trie.insert("tp");
trie.insert("tell");

boolean found = trie.search("teleport"); // true
List<String> matches = trie.getAllMatches("tel"); // ["teleport", "tell"]
```

---

## 完整示例

### 插件主类

```java
package com.example.myplugin;

import com.floyd.core.FloydPlugin;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        logger().info("=================================");
        logger().info("  MyPlugin v1.0.0 已启用");
        logger().info("=================================");
    }
    
    @Override
    protected void cleanup() {
        logger().info("MyPlugin 已禁用");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class[]{PluginConfig.class};
    }
}

@Configuration
@ComponentScan("com.example.myplugin")
class PluginConfig {
    // Spring 配置
}
```

### 命令处理器

```java
package com.example.myplugin.commands;

import com.floyd.core.command.CommandHandler;
import com.floyd.core.command.SubCommandHandler;
import com.floyd.core.permission.RequiredPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class MainCommandHandler {
    
    @CommandHandler("myplugin")
    public void handleMain(CommandSender sender, String[] args) {
        sender.sendMessage("§6MyPlugin v1.0.0");
    }
    
    @SubCommandHandler(parent = "myplugin", name = "reload")
    @RequiredPermission("myplugin.admin.reload")
    public void handleReload(Player player, String[] args) {
        player.sendMessage("§a配置已重载");
    }
}
```

### 服务类

```java
package com.example.myplugin.service;

import com.floyd.core.i18n.I18nMessageProvider;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    
    @Autowired
    private I18nMessageProvider i18n;
    
    public void welcomePlayer(Player player) {
        i18n.sendMessage(player, "welcome.message", player.getName());
    }
}
```

---

## 附录

### 依赖版本

| 依赖 | 版本 |
|------|------|
| Java | 21+ |
| Spring Framework | 6.2.7 |
| PaperMC API | 1.21.11 |
| Lombok | 1.18.30 |
| AspectJ | 1.9.7 |

### 相关链接

- GitHub: https://github.com/codeNoob2281/Floyd-Core
- 问题反馈: https://github.com/codeNoob2281/Floyd-Core/issues
- 讨论区: https://github.com/codeNoob2281/Floyd-Core/discussions

### 许可证

本项目采用 GPL 3.0 许可证。

---

**最后更新**: 2026-05-16