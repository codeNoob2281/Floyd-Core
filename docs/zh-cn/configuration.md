# 配置管理文档

Floyd-Core 提供了类型安全的配置管理系统，支持热重载、默认值管理和配置监听功能。

## 目录

- [概述](#概述)
- [核心组件](#核心组件)
- [基础配置](#基础配置)
- [高级配置](#高级配置)
- [配置热重载](#配置热重载)
- [最佳实践](#最佳实践)

---

## 概述

配置管理系统提供以下功能：

- ✅ 类型安全的配置访问
- ✅ 自动保存默认配置
- ✅ 配置热重载支持
- ✅ 配置变更监听
- ✅ 嵌套配置支持
- ✅ 配置验证
- ✅ 多配置文件管理

---

## 核心组件

### PluginSettingsManager

设置管理器，负责配置的加载、保存和管理。

```java
@Autowired
private PluginSettingsManager settingsManager;
```

### PluginSettingsHolder

配置持有者，用于存储和访问配置数据。

### SettingsReloadAware

配置重载监听接口，实现此接口的 Bean 会在配置重载时收到通知。

```java
@Component
public class MySettings implements SettingsReloadAware {
    @Override
    public void reload(ConfigurationSection config) {
        // 处理配置重载
    }
}
```

---

## 基础配置

### 1. 创建默认配置文件

在 `src/main/resources/` 目录下创建 `config.yml`:

```yaml
# 插件配置
plugin:
  name: "MyPlugin"
  version: "1.0.0"
  debug: false

# 数据库配置
database:
  type: "SQLITE"
  sqlite:
    file: "data.db"
  mysql:
    host: "localhost"
    port: 3306
    database: "myplugin"
    username: "root"
    password: "password"

# 日志配置
logging:
  file:
    enable: true
    filename: "plugin.log"
    max-size: "10MB"
    max-history: 7

# 功能开关
features:
  backpack:
    enabled: true
    max-slots: 54
    upgrade-cost: 1000
  
  teleport:
    enabled: true
    cooldown: 5
    cost: 100

# 消息前缀
messages:
  prefix: "&6[MyPlugin] &r"
  no-permission: "&c你没有权限！"
```

### 2. 访问配置

```java
@Service
public class ConfigService {
    
    @Autowired
    private FloydPlugin plugin;
    
    /**
     * 获取布尔值
     */
    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("plugin.debug", false);
    }
    
    /**
     * 获取整数值
     */
    public int getMaxBackpackSlots() {
        return plugin.getConfig().getInt("features.backpack.max-slots", 54);
    }
    
    /**
     * 获取字符串
     */
    public String getMessagePrefix() {
        return plugin.getConfig().getString("messages.prefix", "&6[Plugin] &r");
    }
    
    /**
     * 获取列表
     */
    public List<String> getEnabledFeatures() {
        return plugin.getConfig().getStringList("features.enabled");
    }
    
    /**
     * 获取嵌套配置
     */
    public ConfigurationSection getDatabaseConfig() {
        return plugin.getConfig().getConfigurationSection("database");
    }
}
```

### 3. 修改配置

```java
@Service
public class ConfigUpdater {
    
    @Autowired
    private FloydPlugin plugin;
    
    /**
     * 更新配置值
     */
    public void setDebugMode(boolean enabled) {
        plugin.getConfig().set("plugin.debug", enabled);
        plugin.saveConfig();
    }
    
    /**
     * 批量更新配置
     */
    public void updateFeatureSettings(boolean enabled, int maxSlots) {
        plugin.getConfig().set("features.backpack.enabled", enabled);
        plugin.getConfig().set("features.backpack.max-slots", maxSlots);
        plugin.saveConfig();
    }
}
```

---

## 高级配置

### 1. 类型安全的配置类

```java
@Component
@ConfigurationProperties(prefix = "features.backpack")
public class BackpackConfig {
    
    private boolean enabled = true;
    private int maxSlots = 54;
    private int upgradeCost = 1000;
    private List<String> disabledWorlds = new ArrayList<>();
    
    // Getters and Setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getMaxSlots() {
        return maxSlots;
    }
    
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }
    
    public int getUpgradeCost() {
        return upgradeCost;
    }
    
    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = upgradeCost;
    }
    
    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }
    
    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }
    
    /**
     * 检查世界是否启用背包
     */
    public boolean isWorldEnabled(World world) {
        return !disabledWorlds.contains(world.getName());
    }
}
```

在 `application.yml` 中配置：

```yaml
features:
  backpack:
    enabled: true
    max-slots: 54
    upgrade-cost: 1000
    disabled-worlds:
      - "world_nether"
      - "world_the_end"
```

使用配置类：

```java
@Service
public class BackpackService {
    
    @Autowired
    private BackpackConfig config;
    
    public void openBackpack(Player player) {
        if (!config.isEnabled()) {
            player.sendMessage("§c背包功能已禁用");
            return;
        }
        
        if (!config.isWorldEnabled(player.getWorld())) {
            player.sendMessage("§c此世界不允许使用背包");
            return;
        }
        
        // 打开背包
        int slots = config.getMaxSlots();
        // ...
    }
}
```

### 2. 配置验证

```java
@Component
public class ConfigValidator {
    
    @Autowired
    private FloydPlugin plugin;
    
    @PostConstruct
    public void validateConfig() {
        ConfigurationSection config = plugin.getConfig();
        
        // 验证必填项
        if (!config.contains("database.type")) {
            throw new IllegalStateException("数据库类型未配置");
        }
        
        // 验证数值范围
        int maxSlots = config.getInt("features.backpack.max-slots");
        if (maxSlots < 9 || maxSlots > 54) {
            throw new IllegalStateException("背包槽位必须在 9-54 之间");
        }
        
        // 验证逻辑一致性
        boolean backpackEnabled = config.getBoolean("features.backpack.enabled");
        boolean teleportEnabled = config.getBoolean("features.teleport.enabled");
        
        if (!backpackEnabled && !teleportEnabled) {
            plugin.getLogger().warning("警告: 所有功能都已禁用");
        }
        
        plugin.getLogger().info("配置验证通过");
    }
}
```

### 3. 多配置文件管理

```java
@Component
public class MultiConfigManager {
    
    @Autowired
    private FloydPlugin plugin;
    
    private FileConfiguration dataConfig;
    private FileConfiguration messagesConfig;
    
    @PostConstruct
    public void loadConfigs() {
        // 加载数据配置
        dataConfig = loadConfig("data.yml");
        
        // 加载消息配置
        messagesConfig = loadConfig("messages.yml");
    }
    
    private FileConfiguration loadConfig(String filename) {
        File file = new File(plugin.getDataFolder(), filename);
        
        if (!file.exists()) {
            plugin.saveResource(filename, false);
        }
        
        return YamlConfiguration.loadConfiguration(file);
    }
    
    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
    
    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
    
    public void saveDataConfig() {
        try {
            dataConfig.save(new File(plugin.getDataFolder(), "data.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("保存数据配置失败: " + e.getMessage());
        }
    }
}
```

### 4. 配置模板

```java
@Service
public class ConfigTemplateService {
    
    @Autowired
    private FloydPlugin plugin;
    
    /**
     * 生成默认配置
     */
    public void generateDefaultConfig() {
        FileConfiguration config = plugin.getConfig();
        
        // 插件基本信息
        config.addDefault("plugin.name", "MyPlugin");
        config.addDefault("plugin.version", "1.0.0");
        config.addDefault("plugin.debug", false);
        
        // 数据库配置
        config.addDefault("database.type", "SQLITE");
        config.addDefault("database.sqlite.file", "data.db");
        
        // 功能配置
        config.addDefault("features.backpack.enabled", true);
        config.addDefault("features.backpack.max-slots", 54);
        
        // 应用默认值
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}
```

---

## 配置热重载

### 1. 实现配置重载监听

```java
@Component
public class PluginSettings implements SettingsReloadAware {
    
    private boolean featureEnabled;
    private int maxItems;
    private String messagePrefix;
    
    @Override
    public void reload(ConfigurationSection config) {
        // 重新加载配置
        this.featureEnabled = config.getBoolean("features.backpack.enabled", true);
        this.maxItems = config.getInt("features.backpack.max-slots", 54);
        this.messagePrefix = config.getString("messages.prefix", "&6[Plugin] &r");
        
        logger().info("配置已重载");
    }
    
    public boolean isFeatureEnabled() {
        return featureEnabled;
    }
    
    public int getMaxItems() {
        return maxItems;
    }
    
    public String getMessagePrefix() {
        return messagePrefix;
    }
}
```

### 2. 重载命令

```java
@SubCommandHandler(parent = "plugin", name = "reload")
@RequiredPermission("myplugin.admin.reload")
public void reloadConfig(Player player, String[] args) {
    try {
        // 重新加载配置文件
        plugin.reloadConfig();
        
        // 通知所有监听器
        settingsManager.reloadAll();
        
        player.sendMessage("§a配置已重新加载！");
        logger().info("管理员 {} 重载了配置", player.getName());
        
    } catch (Exception e) {
        player.sendMessage("§c配置重载失败: " + e.getMessage());
        logger().error("配置重载失败", e);
    }
}
```

### 3. 自动重载

```yaml
# config.yml
settings:
  auto-reload:
    enabled: true
    interval: 300  # 每 5 分钟检查一次
```

```java
@Component
public class AutoReloadTask {
    
    @Autowired
    private FloydPlugin plugin;
    
    @Autowired
    private SettingsManager settingsManager;
    
    private long lastModified = 0;
    
    @Scheduled(fixedRate = 300000) // 每 5 分钟
    public void checkConfigChanges() {
        if (!isAutoReloadEnabled()) {
            return;
        }
        
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        long currentModified = configFile.lastModified();
        
        if (currentModified > lastModified) {
            logger().info("检测到配置文件变更，自动重载...");
            
            try {
                plugin.reloadConfig();
                settingsManager.reloadAll();
                lastModified = currentModified;
                logger().info("配置自动重载成功");
            } catch (Exception e) {
                logger().error("配置自动重载失败", e);
            }
        }
    }
    
    private boolean isAutoReloadEnabled() {
        return plugin.getConfig().getBoolean("settings.auto-reload.enabled", false);
    }
}
```

### 4. 部分重载

```java
@Service
public class PartialReloadService {
    
    @Autowired
    private SettingsManager settingsManager;
    
    /**
     * 只重载特定模块的配置
     */
    public void reloadModule(String module) {
        switch (module.toLowerCase()) {
            case "database":
                reloadDatabaseConfig();
                break;
            case "features":
                reloadFeatureConfig();
                break;
            case "messages":
                reloadMessageConfig();
                break;
            default:
                throw new IllegalArgumentException("未知模块: " + module);
        }
    }
    
    private void reloadDatabaseConfig() {
        ConfigurationSection dbConfig = plugin.getConfig()
            .getConfigurationSection("database");
        
        // 重新初始化数据库连接
        databaseManager.reconnect(dbConfig);
    }
    
    private void reloadFeatureConfig() {
        ConfigurationSection featureConfig = plugin.getConfig()
            .getConfigurationSection("features");
        
        // 更新功能设置
        featureService.updateSettings(featureConfig);
    }
}
```

---

## 最佳实践

### 1. 配置分组

```yaml
# ✅ 好的组织方式
database:
  type: "SQLITE"
  settings:
    pool-size: 10
    timeout: 5000

features:
  backpack:
    enabled: true
    max-slots: 54
  teleport:
    enabled: true
    cooldown: 5

# ❌ 混乱的组织方式
db_type: "SQLITE"
db_pool: 10
backpack_enabled: true
backpack_slots: 54
teleport_enabled: true
```

### 2. 使用注释

```yaml
# 数据库配置
database:
  # 数据库类型: SQLITE, MYSQL, MARIADB
  type: "SQLITE"
  
  # SQLite 配置
  sqlite:
    file: "data.db"  # 数据库文件路径
  
  # MySQL 配置
  mysql:
    host: "localhost"
    port: 3306
```

### 3. 合理的默认值

```java
// ✅ 提供合理的默认值
int maxSlots = config.getInt("features.backpack.max-slots", 54);
boolean enabled = config.getBoolean("features.enabled", true);
String prefix = config.getString("messages.prefix", "&6[Plugin] &r");

// ❌ 不提供默认值
int maxSlots = config.getInt("features.backpack.max-slots"); // 可能返回 0
```

### 4. 配置常量

```java
public final class ConfigKeys {
    
    // 数据库配置
    public static final String DB_TYPE = "database.type";
    public static final String DB_HOST = "database.mysql.host";
    public static final String DB_PORT = "database.mysql.port";
    
    // 功能配置
    public static final String BACKPACK_ENABLED = "features.backpack.enabled";
    public static final String BACKPACK_MAX_SLOTS = "features.backpack.max-slots";
    
    // 消息配置
    public static final String MSG_PREFIX = "messages.prefix";
    public static final String MSG_NO_PERMISSION = "messages.no-permission";
    
    private ConfigKeys() {
        // 防止实例化
    }
}

// 使用常量
boolean enabled = config.getBoolean(ConfigKeys.BACKPACK_ENABLED, true);
```

### 5. 配置备份

```java
@Service
public class ConfigBackupService {
    
    @Autowired
    private FloydPlugin plugin;
    
    /**
     * 备份配置文件
     */
    public void backupConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File backupDir = new File(plugin.getDataFolder(), "backups");
        
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
            .format(new Date());
        
        File backupFile = new File(backupDir, "config_" + timestamp + ".yml");
        
        try {
            Files.copy(configFile.toPath(), backupFile.toPath());
            logger().info("配置备份成功: {}", backupFile.getName());
            
            // 清理旧备份（保留最近 10 个）
            cleanupOldBackups(backupDir, 10);
            
        } catch (IOException e) {
            logger().error("配置备份失败", e);
        }
    }
    
    private void cleanupOldBackups(File backupDir, int keepCount) {
        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".yml"));
        
        if (backups == null || backups.length <= keepCount) {
            return;
        }
        
        Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
        
        for (int i = 0; i < backups.length - keepCount; i++) {
            backups[i].delete();
        }
    }
}
```

### 6. 配置迁移

```java
@Component
public class ConfigMigration {
    
    @Autowired
    private FloydPlugin plugin;
    
    @PostConstruct
    public void migrateConfig() {
        int configVersion = plugin.getConfig().getInt("config-version", 0);
        
        if (configVersion < 1) {
            migrateToV1();
        }
        
        if (configVersion < 2) {
            migrateToV2();
        }
        
        // 更新版本号
        plugin.getConfig().set("config-version", 2);
        plugin.saveConfig();
    }
    
    private void migrateToV1() {
        logger().info("迁移配置: v0 -> v1");
        
        // 旧的配置键 -> 新的配置键
        String oldValue = plugin.getConfig().getString("old_setting");
        if (oldValue != null) {
            plugin.getConfig().set("new.setting", oldValue);
            plugin.getConfig().set("old_setting", null);
        }
    }
    
    private void migrateToV2() {
        logger().info("迁移配置: v1 -> v2");
        
        // 添加新配置项
        if (!plugin.getConfig().contains("features.new-feature")) {
            plugin.getConfig().set("features.new-feature.enabled", true);
        }
    }
}
```

### 7. 配置文档生成

```java
@Service
public class ConfigDocumentationService {
    
    /**
     * 生成配置说明文件
     */
    public void generateConfigDocs() {
        StringBuilder docs = new StringBuilder();
        
        docs.append("# 配置说明\n\n");
        docs.append("## 数据库配置\n\n");
        docs.append("- `database.type`: 数据库类型 (SQLITE, MYSQL)\n");
        docs.append("- `database.sqlite.file`: SQLite 数据库文件路径\n");
        docs.append("\n");
        docs.append("## 功能配置\n\n");
        docs.append("- `features.backpack.enabled`: 是否启用背包功能\n");
        docs.append("- `features.backpack.max-slots`: 背包最大槽位数 (9-54)\n");
        
        File docsFile = new File(plugin.getDataFolder(), "CONFIG_DOCS.md");
        
        try {
            Files.writeString(docsFile.toPath(), docs.toString());
            logger().info("配置文档已生成");
        } catch (IOException e) {
            logger().error("生成配置文档失败", e);
        }
    }
}
```

### 8. 配置验证注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigRange {
    int min();
    int max();
}

public class ValidatedConfig {
    
    @ConfigRange(min = 9, max = 54)
    private int maxSlots;
    
    @ConfigRange(min = 0, max = 1000000)
    private int upgradeCost;
    
    // 验证方法
    public void validate() {
        Field[] fields = getClass().getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigRange.class)) {
                ConfigRange range = field.getAnnotation(ConfigRange.class);
                
                try {
                    field.setAccessible(true);
                    int value = field.getInt(this);
                    
                    if (value < range.min() || value > range.max()) {
                        throw new IllegalStateException(
                            String.format("%s 的值 %d 超出范围 [%d-%d]",
                                field.getName(), value, range.min(), range.max())
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("验证配置失败", e);
                }
            }
        }
    }
}
```

---

## 完整示例

```java
@Component
public class CompleteConfigExample implements SettingsReloadAware {
    
    @Autowired
    private FloydPlugin plugin;
    
    // 配置字段
    private boolean debugMode;
    private String databaseType;
    private int maxBackpackSlots;
    private boolean backpackEnabled;
    private String messagePrefix;
    
    @Override
    public void reload(ConfigurationSection config) {
        // 重新加载所有配置
        this.debugMode = config.getBoolean("plugin.debug", false);
        this.databaseType = config.getString("database.type", "SQLITE");
        this.maxBackpackSlots = config.getInt("features.backpack.max-slots", 54);
        this.backpackEnabled = config.getBoolean("features.backpack.enabled", true);
        this.messagePrefix = config.getString("messages.prefix", "&6[Plugin] &r");
        
        logger().info("配置已重载");
        
        if (debugMode) {
            logger().info("调试模式: 开启");
            logger().info("数据库类型: {}", databaseType);
            logger().info("背包槽位: {}", maxBackpackSlots);
        }
    }
    
    // Getter 方法
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public String getDatabaseType() {
        return databaseType;
    }
    
    public int getMaxBackpackSlots() {
        return maxBackpackSlots;
    }
    
    public boolean isBackpackEnabled() {
        return backpackEnabled;
    }
    
    public String getMessagePrefix() {
        return messagePrefix;
    }
    
    /**
     * 发送带前缀的消息
     */
    public void sendPrefixedMessage(Player player, String message) {
        String coloredPrefix = ChatColor.translateAlternateColorCodes(
            '&', messagePrefix);
        player.sendMessage(coloredPrefix + message);
    }
}
```

---

## 常见问题

### Q1: 配置修改后不生效

**解决方案**:
- 执行重载命令 `/plugin reload`
- 重启服务器
- 检查是否有自动重载功能

### Q2: 配置保存失败

**解决方案**:
- 检查文件权限
- 确认磁盘空间充足
- 查看日志中的错误信息

### Q3: 配置格式错误

**解决方案**:
- 使用 YAML 验证工具检查语法
- 确保缩进正确（使用空格，不是 Tab）
- 检查特殊字符是否需要引号

### Q4: 默认配置不生成

**解决方案**:
- 确认 `config.yml` 在 `src/main/resources/` 目录下
- 检查 `saveDefaultConfig()` 是否被调用
- 删除现有配置文件让插件重新生成

---

## 总结

Floyd-Core 的配置管理系统提供了：

- ✅ 类型安全的配置访问
- ✅ 灵活的重载机制
- ✅ 完善的验证功能
- ✅ 多配置文件支持
- ✅ 配置迁移工具

合理使用配置管理系统可以让插件配置更加规范、安全和易维护。