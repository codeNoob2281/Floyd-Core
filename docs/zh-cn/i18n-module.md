# 国际化模块文档

Floyd-Core 的国际化（i18n）模块提供了完整的多语言支持，包括消息管理、动态语言切换和占位符替换功能。

## 目录

- [概述](#概述)
- [核心组件](#核心组件)
- [配置语言文件](#配置语言文件)
- [基本用法](#基本用法)
- [高级特性](#高级特性)
- [语言切换](#语言切换)
- [最佳实践](#最佳实践)

---

## 概述

国际化模块提供以下功能：

- ✅ 多语言消息管理
- ✅ YAML 格式语言文件
- ✅ 动态语言切换
- ✅ 消息占位符支持
- ✅ 默认语言回退
- ✅ 玩家级别语言设置
- ✅ 服务器级别语言设置

---

## 核心组件

### I18nMessageProvider

消息提供者接口，负责获取和管理翻译消息。

```java
@Autowired
private I18nMessageProvider i18nProvider;
```

### DefaultI18nMessageProvider

默认的消息提供者实现。

### I18nSettingManager

语言设置管理器，负责管理玩家和服务器的语言偏好。

```java
@Autowired
private I18nSettingManager settingManager;
```

### LocaleMessage

区域消息类，封装了消息键和参数。

---

## 配置语言文件

### 1. 创建语言文件

在 `src/main/resources/language/` 目录下创建语言文件：

**zh_cn.yml** (简体中文)
```yaml
# 通用消息
common:
  success: "§a成功!"
  error: "§c错误: {0}"
  no-permission: "§c你没有权限执行此操作!"
  reload: "§a配置已重新加载!"

# 欢迎消息
welcome:
  message: "§6欢迎, §e{0}§6! 欢迎来到服务器!"
  first-join: "§e{0} §6首次加入服务器!"

# 命令消息
commands:
  help:
    title: "§6§l=== 帮助 ==="
    usage: "§e用法: §f{0}"
    description: "§7{0}"
  
  teleport:
    success: "§a已传送到 {0}"
    not-found: "§c玩家不存在: {0}"
    cooldown: "§c传送冷却中，请等待 {0} 秒"

# 物品消息
items:
  give: "§a你获得了 {0} x {1}"
  take: "§c失去了 {0} x {1}"
  full: "§c背包已满!"

# 经济消息
economy:
  balance: "§e你的余额: §f${0}"
  pay-success: "§a成功支付 ${0} 给 {1}"
  insufficient: "§c余额不足! 当前余额: ${0}"
```

**en.yml** (英语)
```yaml
common:
  success: "§aSuccess!"
  error: "§cError: {0}"
  no-permission: "§cYou don't have permission!"
  reload: "§aConfiguration reloaded!"

welcome:
  message: "§6Welcome, §e{0}§6! Welcome to the server!"
  first-join: "§e{0} §6joined for the first time!"

commands:
  help:
    title: "§6§l=== Help ==="
    usage: "§eUsage: §f{0}"
    description: "§7{0}"
  
  teleport:
    success: "§aTeleported to {0}"
    not-found: "§cPlayer not found: {0}"
    cooldown: "§cTeleport on cooldown, wait {0} seconds"

items:
  give: "§aYou received {0} x {1}"
  take: "§cLost {0} x {1}"
  full: "§cInventory is full!"

economy:
  balance: "§eYour balance: §f${0}"
  pay-success: "§aSuccessfully paid ${0} to {1}"
  insufficient: "§cInsufficient funds! Current balance: ${0}"
```

**ja.yml** (日语)
```yaml
common:
  success: "§a成功！"
  error: "§cエラー: {0}"
  no-permission: "§c権限がありません！"
  reload: "§a設定をリロードしました！"

welcome:
  message: "§6ようこそ、§e{0}§6さん！"
  first-join: "§e{0} §6さんが初参加しました！"
```

### 2. 配置默认语言

在 `config.yml` 中配置：

```yaml
i18n:
  default-locale: "zh_cn"     # 默认语言
  auto-detect: true           # 自动检测客户端语言
  fallback-to-default: true   # 缺失消息时回退到默认语言
  
  # 语言文件路径
  language-dir: "language"
  
  # 支持的语言列表
  supported-locales:
    - "zh_cn"
    - "en"
    - "ja"
```

### 3. 初始化国际化模块

```java
@Component
public class I18nInitializer {
    
    @Autowired
    private I18nSettingManager settingManager;
    
    @PostConstruct
    public void init() {
        // 加载语言文件
        settingManager.loadLanguageFiles();
        
        logger().info("国际化模块初始化完成");
    }
}
```

---

## 基本用法

### 1. 获取消息

```java
@Service
public class MessageService {
    
    @Autowired
    private I18nMessageProvider i18nProvider;
    
    /**
     * 获取简单消息
     */
    public String getWelcomeMessage(Player player) {
        return i18nProvider.getMessage(player, "welcome.message");
    }
    
    /**
     * 获取带参数的消息
     */
    public String getBalanceMessage(Player player, int balance) {
        return i18nProvider.getMessage(player, "economy.balance", balance);
    }
    
    /**
     * 获取带多个参数的消息
     */
    public String getItemGiveMessage(Player player, String itemName, int amount) {
        return i18nProvider.getMessage(player, "items.give", itemName, amount);
    }
}
```

### 2. 发送消息给玩家

```java
@SubCommandHandler(parent = "balance", name = "check")
public void checkBalance(Player player, String[] args) {
    int balance = getBalance(player);
    
    // 直接发送消息
    i18nProvider.sendMessage(player, "economy.balance", balance);
    
    // 或者获取消息后自行处理
    String message = i18nProvider.getMessage(player, "economy.balance", balance);
    player.sendMessage(message);
}
```

### 3. 获取指定语言的消息

```java
/**
 * 获取特定语言的消息
 */
public String getMessageInLocale(String locale, String key, Object... args) {
    return i18nProvider.getMessage(locale, key, args);
}

// 使用示例
String zhMsg = getMessageInLocale("zh_cn", "common.success");
String enMsg = getMessageInLocale("en", "common.success");
```

### 4. 检查消息是否存在

```java
/**
 * 检查消息键是否存在
 */
public boolean hasMessage(Player player, String key) {
    return i18nProvider.hasMessage(player, key);
}

// 使用示例
if (i18nProvider.hasMessage(player, "custom.welcome")) {
    player.sendMessage(i18nProvider.getMessage(player, "custom.welcome"));
} else {
    player.sendMessage("Default welcome message");
}
```

---

## 高级特性

### 1. 消息占位符

支持多种占位符格式：

```yaml
messages:
  simple: "你好, {0}!"
  multiple: "{0} 给了 {1} x {2}"
  formatted: "余额: ${0,number,#.##}"
  date: "最后登录: {0,date,yyyy-MM-dd}"
```

```java
// 简单占位符
String msg = i18nProvider.getMessage(player, "messages.simple", playerName);
// 输出: "你好, Steve!"

// 多个占位符
String msg = i18nProvider.getMessage(player, "messages.multiple", 
                                     senderName, itemName, amount);
// 输出: "Steve 给了 Diamond x 64"

// 格式化数字
String msg = i18nProvider.getMessage(player, "messages.formatted", 1234.567);
// 输出: "余额: $1,234.57"
```

### 2. 嵌套消息

```yaml
commands:
  teleport:
    messages:
      success: "§a传送成功!"
      error: "§c传送失败!"
      cooldown: "§c冷却中..."
```

```java
String success = i18nProvider.getMessage(player, "commands.teleport.messages.success");
```

### 3. 消息模板

```yaml
templates:
  prefix: "§6[MyPlugin] §r"
  success: "{prefix} §a{message}"
  error: "{prefix} §c{message}"
  warning: "{prefix} §e{message}"
```

```java
// 先获取前缀
String prefix = i18nProvider.getMessage(player, "templates.prefix");

// 组合消息
String success = i18nProvider.getMessage(player, "templates.success", 
                                         prefix, "操作成功");
// 输出: "[MyPlugin] 操作成功"
```

### 4. 颜色代码支持

所有消息都支持 Minecraft 颜色代码：

```yaml
colors:
  primary: "§6"      # 金色
  secondary: "§e"    # 黄色
  success: "§a"      # 绿色
  error: "§c"        # 红色
  info: "§b"         # 青色
```

```java
String title = i18nProvider.getMessage(player, "colors.primary") + "标题";
```

### 5. 多行消息

```yaml
help:
  title: |
    §6§l=== 插件帮助 ===
    §e/plugin info §7- 查看信息
    §e/plugin reload §7- 重载配置
    §e/plugin help §7- 显示此帮助
```

```java
// 发送多行消息
String helpText = i18nProvider.getMessage(player, "help.title");
player.sendMessage(helpText);
```

### 6. 条件消息

```java
/**
 * 根据条件选择不同的消息
 */
public String getResultMessage(Player player, boolean success) {
    if (success) {
        return i18nProvider.getMessage(player, "common.success");
    } else {
        return i18nProvider.getMessage(player, "common.error", "操作失败");
    }
}
```

---

## 语言切换

### 1. 玩家级别语言设置

```java
@Service
public class LanguageService {
    
    @Autowired
    private I18nSettingManager settingManager;
    
    /**
     * 设置玩家语言
     */
    public void setPlayerLanguage(Player player, String locale) {
        settingManager.setPlayerLocale(player.getUniqueId(), locale);
        player.sendMessage("§a语言已设置为: " + locale);
    }
    
    /**
     * 获取玩家语言
     */
    public String getPlayerLanguage(Player player) {
        return settingManager.getPlayerLocale(player.getUniqueId());
    }
    
    /**
     * 重置为默认语言
     */
    public void resetPlayerLanguage(Player player) {
        settingManager.resetPlayerLocale(player.getUniqueId());
        player.sendMessage("§a语言已重置为默认");
    }
}
```

### 2. 语言切换命令

```java
@SubCommandHandler(parent = "lang", name = "set")
public void setLanguage(Player player, String[] args) {
    if (args.length < 1) {
        player.sendMessage("§c用法: /lang set <语言>");
        player.sendMessage("§e可用语言: zh_cn, en, ja");
        return;
    }
    
    String locale = args[0].toLowerCase();
    
    // 验证语言是否支持
    if (!i18nProvider.isSupported(locale)) {
        player.sendMessage("§c不支持的语言: " + locale);
        return;
    }
    
    // 设置语言
    languageService.setPlayerLanguage(player, locale);
}

@SubCommandHandler(parent = "lang", name = "list")
public void listLanguages(Player player, String[] args) {
    player.sendMessage("§6§l=== 可用语言 ===");
    player.sendMessage("§ezh_cn §7- 简体中文");
    player.sendMessage("§een §7- English");
    player.sendMessage("§eja §7- 日本語");
}
```

### 3. 自动检测客户端语言

```yaml
i18n:
  auto-detect: true  # 启用自动检测
```

```java
/**
 * 根据客户端语言自动设置
 */
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    if (settingManager.isAutoDetectEnabled()) {
        // 尝试从客户端获取语言
        String clientLocale = player.getClientLocale();
        
        if (i18nProvider.isSupported(clientLocale)) {
            settingManager.setPlayerLocale(player.getUniqueId(), clientLocale);
            logger().debug("自动设置玩家 {} 的语言为 {}", player.getName(), clientLocale);
        }
    }
}
```

### 4. 服务器级别语言设置

```java
/**
 * 设置服务器默认语言
 */
public void setServerDefaultLanguage(String locale) {
    settingManager.setDefaultLocale(locale);
    logger().info("服务器默认语言已设置为: {}", locale);
}

/**
 * 获取服务器默认语言
 */
public String getServerDefaultLanguage() {
    return settingManager.getDefaultLocale();
}
```

---

## 最佳实践

### 1. 消息键命名规范

使用层次化的命名结构：

```yaml
# ✅ 好的命名
plugin-name:
  commands:
    teleport:
      success: "..."
      error: "..."
  items:
    give: "..."
    take: "..."

# ❌ 避免的命名
msg1: "..."
teleport_ok: "..."
item_give_message: "..."
```

### 2. 集中管理消息键

```java
public final class MessageKeys {
    
    // 通用消息
    public static final String SUCCESS = "common.success";
    public static final String ERROR = "common.error";
    public static final String NO_PERMISSION = "common.no-permission";
    
    // 命令消息
    public static final String CMD_TELEPORT_SUCCESS = "commands.teleport.success";
    public static final String CMD_TELEPORT_NOT_FOUND = "commands.teleport.not-found";
    
    // 经济消息
    public static final String ECO_BALANCE = "economy.balance";
    public static final String ECO_INSUFFICIENT = "economy.insufficient";
    
    private MessageKeys() {
        // 防止实例化
    }
}

// 使用常量
player.sendMessage(i18nProvider.getMessage(player, MessageKeys.SUCCESS));
```

### 3. 避免硬编码消息

```java
// ❌ 硬编码
player.sendMessage("§a操作成功!");

// ✅ 使用国际化
player.sendMessage(i18nProvider.getMessage(player, "common.success"));
```

### 4. 提供有意义的默认值

```java
/**
 * 获取消息，如果不存在则返回默认值
 */
public String getMessageOrDefault(Player player, String key, String defaultValue, Object... args) {
    if (i18nProvider.hasMessage(player, key)) {
        return i18nProvider.getMessage(player, key, args);
    }
    return MessageFormat.format(defaultValue, args);
}

// 使用示例
String msg = getMessageOrDefault(player, "custom.message", 
                                 "Default message: {0}", paramName);
```

### 5. 懒加载语言文件

```java
@Component
public class LazyI18nLoader {
    
    private final Map<String, Properties> loadedLocales = new ConcurrentHashMap<>();
    
    @Autowired
    private I18nMessageProvider i18nProvider;
    
    /**
     * 按需加载语言
     */
    public Properties getLocale(String locale) {
        return loadedLocales.computeIfAbsent(locale, this::loadLocale);
    }
    
    private Properties loadLocale(String locale) {
        logger().debug("加载语言文件: {}", locale);
        return i18nProvider.loadLocale(locale);
    }
}
```

### 6. 消息验证

```java
@Component
public class MessageValidator {
    
    @Autowired
    private I18nMessageProvider i18nProvider;
    
    /**
     * 验证所有语言文件的一致性
     */
    @PostConstruct
    public void validateMessages() {
        List<String> supportedLocales = i18nProvider.getSupportedLocales();
        String defaultLocale = i18nProvider.getDefaultLocale();
        
        // 获取默认语言的所有消息键
        Set<String> defaultKeys = i18nProvider.getAllMessageKeys(defaultLocale);
        
        // 检查其他语言是否包含所有键
        for (String locale : supportedLocales) {
            if (locale.equals(defaultLocale)) continue;
            
            Set<String> localeKeys = i18nProvider.getAllMessageKeys(locale);
            
            for (String key : defaultKeys) {
                if (!localeKeys.contains(key)) {
                    logger().warning("语言 {} 缺少消息键: {}", locale, key);
                }
            }
        }
    }
}
```

### 7. 性能优化

```java
@Service
public class CachedMessageService {
    
    private final Map<String, String> messageCache = new ConcurrentHashMap<>();
    
    @Autowired
    private I18nMessageProvider i18nProvider;
    
    /**
     * 缓存常用消息
     */
    public String getCachedMessage(Player player, String key) {
        String cacheKey = player.getUniqueId() + ":" + key;
        
        return messageCache.computeIfAbsent(cacheKey, k -> {
            return i18nProvider.getMessage(player, key);
        });
    }
    
    /**
     * 清除缓存
     */
    public void clearCache(UUID playerId) {
        messageCache.keySet().removeIf(key -> key.startsWith(playerId.toString()));
    }
}
```

### 8. 动态消息更新

```java
/**
 * 热重载语言文件
 */
@SubCommandHandler(parent = "i18n", name = "reload")
@RequiredPermission("myplugin.admin.i18n")
public void reloadLanguages(Player player, String[] args) {
    try {
        i18nProvider.reloadAllLocales();
        player.sendMessage(i18nProvider.getMessage(player, "common.reload"));
        logger().info("语言文件已重载");
    } catch (Exception e) {
        player.sendMessage("§c重载失败: " + e.getMessage());
        logger().error("语言文件重载失败", e);
    }
}
```

---

## 完整示例

```java
@Service
public class CompleteI18nExample {
    
    @Autowired
    private I18nMessageProvider i18nProvider;
    
    @Autowired
    private I18nSettingManager settingManager;
    
    /**
     * 玩家加入时的欢迎消息
     */
    public void sendWelcomeMessage(Player player) {
        boolean isFirstJoin = !player.hasPlayedBefore();
        
        if (isFirstJoin) {
            i18nProvider.sendMessage(player, "welcome.first-join", 
                                    player.getName());
        } else {
            i18nProvider.sendMessage(player, "welcome.message", 
                                    player.getName());
        }
    }
    
    /**
     * 传送命令
     */
    public void teleportPlayer(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            i18nProvider.sendMessage(sender, "commands.teleport.not-found", 
                                    targetName);
            return;
        }
        
        sender.teleport(target.getLocation());
        i18nProvider.sendMessage(sender, "commands.teleport.success", 
                                target.getName());
    }
    
    /**
     * 支付系统
     */
    public boolean processPayment(Player payer, Player receiver, int amount) {
        int balance = getBalance(payer);
        
        if (balance < amount) {
            i18nProvider.sendMessage(payer, "economy.insufficient", balance);
            return false;
        }
        
        // 执行支付
        deductBalance(payer, amount);
        addBalance(receiver, amount);
        
        i18nProvider.sendMessage(payer, "economy.pay-success", amount, 
                                receiver.getName());
        i18nProvider.sendMessage(receiver, "economy.received", amount, 
                                payer.getName());
        
        return true;
    }
    
    /**
     * 发送帮助信息
     */
    public void sendHelp(CommandSender sender) {
        i18nProvider.sendMessage(sender, "commands.help.title");
        i18nProvider.sendMessage(sender, "commands.help.usage", "/plugin info");
        i18nProvider.sendMessage(sender, "commands.help.description", "查看插件信息");
    }
}
```

---

## 常见问题

### Q1: 消息显示为键名而不是翻译

**解决方案**:
- 检查语言文件是否正确加载
- 确认消息键拼写正确
- 验证语言文件格式（YAML）

### Q2: 占位符没有被替换

**解决方案**:
- 确保传递了正确数量的参数
- 检查占位符格式 `{0}`, `{1}` 等
- 验证参数类型是否匹配

### Q3: 中文显示乱码

**解决方案**:
- 确保 YAML 文件使用 UTF-8 编码
- 在 Maven 配置中设置编码：
  ```xml
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  ```

### Q4: 语言切换不生效

**解决方案**:
- 确认玩家语言设置已保存
- 检查语言是否在被支持的列表中
- 验证语言文件存在且格式正确

---

## 总结

Floyd-Core 的国际化模块提供了：

- ✅ 完整的多语言支持
- ✅ 灵活的消息管理
- ✅ 动态语言切换
- ✅ 占位符和格式化
- ✅ 性能优化机制

合理使用国际化模块可以让插件支持全球玩家，提升用户体验。