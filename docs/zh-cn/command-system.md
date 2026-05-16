# 命令系统文档

Floyd-Core 提供了强大的命令处理系统，支持子命令、参数补全、权限检查和灵活的命令分发机制。

## 目录

- [概述](#概述)
- [核心组件](#核心组件)
- [基础用法](#基础用法)
- [子命令系统](#子命令系统)
- [命令补全](#命令补全)
- [权限集成](#权限集成)
- [高级特性](#高级特性)
- [最佳实践](#最佳实践)

---

## 概述

命令系统是 Floyd-Core 的核心功能之一，提供：

- ✅ 注解驱动的命令注册
- ✅ 多级子命令支持
- ✅ 智能参数补全（Tab Complete）
- ✅ 自动权限检查
- ✅ 灵活的参数解析
- ✅ 命令帮助生成

---

## 核心组件

### CommandDispatcher

命令分发器，负责接收和分发命令到对应的处理器。

```java
@Component
public class CommandConfig {
    
    @Bean
    public CommandDispatcher commandDispatcher() {
        return new CommandDispatcher();
    }
}
```

### CommandHandler

命令处理器接口，所有命令处理器都需要实现此接口或使用注解。

### SubCommandHandler

子命令处理器，用于处理主命令下的子命令。

### TrieCommandCompleter

基于 Trie 树的命令补全器，提供高效的 Tab Complete 功能。

---

## 基础用法

### 1. 创建命令处理器

```java
@Component
public class MyCommandHandler {
    
    /**
     * 处理主命令 /myplugin
     */
    @CommandHandler("myplugin")
    public void handleMain(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // 显示帮助信息
            showHelp(sender);
            return;
        }
        
        sender.sendMessage("§6MyPlugin v1.0.0");
        sender.sendMessage("§e使用 /myplugin help 查看帮助");
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== MyPlugin 帮助 ===");
        sender.sendMessage("§e/myplugin info §7- 查看插件信息");
        sender.sendMessage("§e/myplugin reload §7- 重载配置");
    }
}
```

### 2. 带参数的命令

```java
@CommandHandler("teleport")
public void handleTeleport(Player player, String[] args) {
    if (args.length < 1) {
        player.sendMessage("§c用法: /teleport <玩家名>");
        return;
    }
    
    String targetName = args[0];
    Player target = Bukkit.getPlayer(targetName);
    
    if (target == null) {
        player.sendMessage("§c玩家不在线: " + targetName);
        return;
    }
    
    player.teleport(target.getLocation());
    player.sendMessage("§a已传送到 " + target.getName());
}
```

### 3. 多个命令处理器

```java
@Component
public class AdminCommandHandler {
    
    @CommandHandler("admin")
    @RequiredPermission("myplugin.admin")
    public void handleAdmin(Player player, String[] args) {
        player.sendMessage("§c管理员命令面板");
    }
}

@Component
public class PlayerCommandHandler {
    
    @CommandHandler("balance")
    public void handleBalance(Player player, String[] args) {
        player.sendMessage("§e你的余额: §f$1000");
    }
}
```

---

## 子命令系统

子命令系统允许创建层次化的命令结构，如 `/myplugin subcommand action`。

### 1. 定义子命令处理器

```java
@Component
public class BackpackCommandHandler {
    
    /**
     * 主命令处理器
     */
    @CommandHandler("backpack")
    public void handleBackpack(Player player, String[] args) {
        if (args.length == 0) {
            showHelp(player);
            return;
        }
        // 子命令会自动分发
    }
    
    /**
     * 打开背包子命令
     */
    @SubCommandHandler(parent = "backpack", name = "open")
    public void openBackpack(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§c用法: /backpack open <ID>");
            return;
        }
        
        int backpackId = Integer.parseInt(args[0]);
        // 打开背包逻辑
        player.sendMessage("§a已打开背包 #" + backpackId);
    }
    
    /**
     * 升级背包子命令
     */
    @SubCommandHandler(parent = "backpack", name = "upgrade")
    @RequiredPermission("myplugin.backpack.upgrade")
    public void upgradeBackpack(Player player, String[] args) {
        // 升级逻辑
        player.sendMessage("§a背包升级成功!");
    }
    
    /**
     * 清理背包子命令
     */
    @SubCommandHandler(parent = "backpack", name = "clear")
    @RequiredPermission("myplugin.backpack.clear")
    public void clearBackpack(Player player, String[] args) {
        // 清理逻辑
        player.sendMessage("§a背包已清理");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6§l=== 背包命令 ===");
        player.sendMessage("§e/backpack open <ID> §7- 打开背包");
        player.sendMessage("§e/backpack upgrade §7- 升级背包");
        player.sendMessage("§e/backpack clear §7- 清理背包");
    }
}
```

### 2. 嵌套子命令

```java
@Component
public class GuildCommandHandler {
    
    @CommandHandler("guild")
    public void handleGuild(Player player, String[] args) {
        // 主命令
    }
    
    // /guild create
    @SubCommandHandler(parent = "guild", name = "create")
    public void createGuild(Player player, String[] args) {
        // 创建公会
    }
    
    // /guild invite
    @SubCommandHandler(parent = "guild", name = "invite")
    public void invitePlayer(Player player, String[] args) {
        // 邀请玩家
    }
    
    // /guild admin kick (二级子命令)
    @SubCommandHandler(parent = "guild.admin", name = "kick")
    @RequiredPermission("myplugin.guild.admin")
    public void kickPlayer(Player player, String[] args) {
        // 踢出玩家
    }
}
```

### 3. 子命令参数处理

```java
@SubCommandHandler(parent = "item", name = "give")
public void giveItem(Player player, String[] args) {
    if (args.length < 2) {
        player.sendMessage("§c用法: /item give <物品> <数量>");
        return;
    }
    
    String itemName = args[0];
    int amount = Integer.parseInt(args[1]);
    
    Material material = Material.matchMaterial(itemName);
    if (material == null) {
        player.sendMessage("§c无效的物品: " + itemName);
        return;
    }
    
    ItemStack item = new ItemStack(material, amount);
    player.getInventory().addItem(item);
    player.sendMessage("§a已获得 " + amount + " x " + itemName);
}
```

---

## 命令补全

TrieCommandCompleter 提供智能的 Tab Complete 功能。

### 1. 基础补全

```java
@Component
public class MyCommandCompleter implements CommandCompleter {
    
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // 补主子命令
            return Arrays.asList("info", "reload", "help");
        }
        
        if (args.length == 2 && args[0].equals("teleport")) {
            // 补全在线玩家名
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
}
```

### 2. Trie 树补全

```java
@Configuration
public class CompleterConfig {
    
    @Bean
    public TrieCommandCompleter trieCommandCompleter() {
        TrieCommandCompleter completer = new TrieCommandCompleter();
        
        // 添加命令路径
        completer.addCommandPath("backpack", "open", "close", "upgrade", "clear");
        completer.addCommandPath("guild", "create", "invite", "leave", "disband");
        completer.addCommandPath("item", "give", "take", "clear");
        
        return completer;
    }
}
```

### 3. 动态补全

```java
@SubCommandHandler(parent = "warp", name = "goto")
public class WarpCompleter implements CommandCompleter {
    
    @Autowired
    private WarpService warpService;
    
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // 从服务获取所有传送点
            return warpService.getAllWarpNames();
        }
        return Collections.emptyList();
    }
}
```

### 4. 权限感知补全

```java
@Component
public class PermAwareCompleter extends TrieCommandCompleter {
    
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = super.complete(sender, args);
        
        // 过滤没有权限的命令
        return completions.stream()
            .filter(cmd -> hasPermission(sender, cmd))
            .collect(Collectors.toList());
    }
    
    private boolean hasPermission(CommandSender sender, String command) {
        // 检查权限逻辑
        return sender.hasPermission("myplugin.command." + command);
    }
}
```

---

## 权限集成

命令系统与权限模块无缝集成，自动进行权限检查。

### 1. 命令级权限

```java
@CommandHandler("admin")
@RequiredPermission("myplugin.admin.use")
public void handleAdmin(Player player, String[] args) {
    // 只有拥有 myplugin.admin.use 权限的玩家才能执行
}
```

### 2. 子命令权限

```java
@SubCommandHandler(parent = "backpack", name = "upgrade")
@RequiredPermission("myplugin.backpack.upgrade")
public void upgradeBackpack(Player player, String[] args) {
    // 需要特定权限才能升级背包
}
```

### 3. 动态权限检查

```java
@SubCommandHandler(parent = "bank", name = "withdraw")
public void withdraw(Player player, String[] args) {
    // 在方法内进行动态权限检查
    if (!player.hasPermission("myplugin.bank.withdraw.unlimited") 
        && amount > 10000) {
        player.sendMessage("§c单次最多提取 $10,000");
        return;
    }
    
    // 提款逻辑
}
```

---

## 高级特性

### 1. 命令拦截器

```java
@Component
public class CommandInterceptor {
    
    @Autowired
    private Logger logger;
    
    /**
     * 记录所有命令执行
     */
    @Before("@annotation(CommandHandler)")
    public void logCommand(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        CommandSender sender = (CommandSender) args[0];
        String command = extractCommandName(joinPoint);
        
        logger.info("玩家 {} 执行命令: {}", sender.getName(), command);
    }
}
```

### 2. 命令帮助生成器

```java
@Service
public class HelpGenerator {
    
    @Autowired
    private CommandDispatcher dispatcher;
    
    public void sendHelp(CommandSender sender, String command) {
        List<SubCommandMapping> subCommands = 
            dispatcher.getSubCommands(command);
        
        sender.sendMessage("§6§l=== " + command + " 帮助 ===");
        
        for (SubCommandMapping mapping : subCommands) {
            String usage = buildUsage(mapping);
            String description = mapping.getDescription();
            
            sender.sendMessage("§e" + usage + " §7- " + description);
        }
    }
    
    private String buildUsage(SubCommandMapping mapping) {
        return "/" + mapping.getParent() + " " + mapping.getName();
    }
}
```

### 3. 命令冷却时间

```java
@Service
public class CommandCooldownService {
    
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    
    public boolean isOnCooldown(Player player, String command) {
        Long lastUse = cooldowns.get(player.getUniqueId());
        if (lastUse == null) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - lastUse;
        return elapsed < getCooldownTime(command);
    }
    
    public void setCooldown(Player player, String command) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    private long getCooldownTime(String command) {
        // 根据不同命令返回不同冷却时间
        switch (command) {
            case "teleport": return 5000;  // 5秒
            case "heal": return 30000;     // 30秒
            default: return 1000;          // 1秒
        }
    }
}

// 在命令中使用
@SubCommandHandler(parent = "tp", name = "home")
public void teleportHome(Player player, String[] args) {
    if (cooldownService.isOnCooldown(player, "teleport")) {
        player.sendMessage("§c命令冷却中，请稍后再试");
        return;
    }
    
    // 传送逻辑
    cooldownService.setCooldown(player, "teleport");
}
```

### 4. 命令别名

```java
@CommandHandler(value = "teleport", aliases = {"tp", "goto"})
public void handleTeleport(Player player, String[] args) {
    // 可以通过 /teleport, /tp, /goto 访问
}
```

### 5. 异步命令处理

```java
@SubCommandHandler(parent = "stats", name = "calculate")
public void calculateStats(Player player, String[] args) {
    // 在异步线程中执行耗时操作
    CompletableFuture.runAsync(() -> {
        // 耗时的计算逻辑
        StatsResult result = performComplexCalculation(player);
        
        // 回到主线程发送结果
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§a计算完成!");
            player.sendMessage("§e击杀数: " + result.getKills());
        });
    });
}
```

---

## 最佳实践

### 1. 命令组织

按功能模块组织命令处理器：

```
com.example.plugin.commands
├── admin/
│   ├── AdminCommandHandler.java
│   └── DebugCommandHandler.java
├── player/
│   ├── BalanceCommandHandler.java
│   └── TeleportCommandHandler.java
├── guild/
│   ├── GuildCommandHandler.java
│   └── GuildAdminCommandHandler.java
└── item/
    ├── ItemCommandHandler.java
    └── ShopCommandHandler.java
```

### 2. 输入验证

始终验证用户输入：

```java
@SubCommandHandler(parent = "item", name = "give")
public void giveItem(Player player, String[] args) {
    // 验证参数数量
    if (args.length < 2) {
        player.sendMessage("§c用法: /item give <物品> <数量>");
        return;
    }
    
    // 验证物品名称
    Material material = Material.matchMaterial(args[0]);
    if (material == null) {
        player.sendMessage("§c无效的物品: " + args[0]);
        return;
    }
    
    // 验证数量
    int amount;
    try {
        amount = Integer.parseInt(args[1]);
        if (amount <= 0 || amount > 64) {
            player.sendMessage("§c数量必须在 1-64 之间");
            return;
        }
    } catch (NumberFormatException e) {
        player.sendMessage("§c无效的数量: " + args[1]);
        return;
    }
    
    // 执行业务逻辑
    giveItemToPlayer(player, material, amount);
}
```

### 3. 友好的错误提示

```java
// ❌ 不好的做法
if (target == null) {
    player.sendMessage("错误");
}

// ✅ 好的做法
if (target == null) {
    player.sendMessage("§c找不到玩家: §e" + targetName);
    player.sendMessage("§7提示: 请检查玩家名称是否正确");
}
```

### 4. 权限消息自定义

```java
@SubCommandHandler(parent = "admin", name = "ban")
@RequiredPermission(
    value = "myplugin.admin.ban",
    message = "§c你没有封禁玩家的权限!"
)
public void banPlayer(Player player, String[] args) {
    // 封禁逻辑
}
```

### 5. 命令日志

```java
@Aspect
@Component
public class CommandLoggingAspect {
    
    @Autowired
    private Logger logger;
    
    @AfterReturning("@annotation(CommandHandler) || @annotation(SubCommandHandler)")
    public void logCommandExecution(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof CommandSender) {
            CommandSender sender = (CommandSender) args[0];
            String methodName = joinPoint.getSignature().getName();
            
            logger.debug("命令执行: {} by {}", methodName, sender.getName());
        }
    }
}
```

### 6. 避免硬编码

```java
// ❌ 硬编码消息
player.sendMessage("你没有权限");

// ✅ 使用国际化
@Autowired
private I18nMessageProvider i18n;

player.sendMessage(i18n.getMessage(player, "error.no-permission"));
```

### 7. 性能优化

```java
// ❌ 每次调用都创建对象
public List<String> complete(...) {
    return new ArrayList<>(Arrays.asList("cmd1", "cmd2", "cmd3"));
}

// ✅ 使用常量
private static final List<String> COMMANDS = 
    Arrays.asList("cmd1", "cmd2", "cmd3");

public List<String> complete(...) {
    return COMMANDS;
}
```

---

## 完整示例

```java
@Component
public class CompleteCommandExample {
    
    @Autowired
    private I18nMessageProvider i18n;
    
    @Autowired
    private DatabaseManager db;
    
    /**
     * 主命令
     */
    @CommandHandler("example")
    public void handleMain(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return;
        }
    }
    
    /**
     * 查询子命令
     */
    @SubCommandHandler(parent = "example", name = "query")
    public void query(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(i18n.getMessage(player, "error.missing-args"));
            return;
        }
        
        String key = args[0];
        
        // 异步查询数据库
        CompletableFuture.supplyAsync(() -> {
            return db.query("SELECT value FROM data WHERE key = ?", key);
        }).thenAccept(result -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (result != null) {
                    player.sendMessage("§a结果: " + result);
                } else {
                    player.sendMessage("§c未找到数据");
                }
            });
        });
    }
    
    /**
     * 设置子命令（需要权限）
     */
    @SubCommandHandler(parent = "example", name = "set")
    @RequiredPermission("example.admin.set")
    public void set(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c用法: /example set <key> <value>");
            return;
        }
        
        String key = args[0];
        String value = args[1];
        
        db.execute("INSERT OR REPLACE INTO data (key, value) VALUES (?, ?)", 
                   key, value);
        
        player.sendMessage("§a设置成功: " + key + " = " + value);
    }
    
    /**
     * 显示帮助
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== Example Plugin ===");
        sender.sendMessage("§e/example query <key> §7- 查询数据");
        
        if (sender.hasPermission("example.admin.set")) {
            sender.sendMessage("§e/example set <key> <value> §7- 设置数据");
        }
    }
}
```

---

## 常见问题

### Q1: 命令没有被识别

**解决方案**:
- 确保在 `plugin.yml` 中注册了命令
- 检查 `@CommandHandler` 注解的值是否正确
- 确认 Spring Bean 扫描包含了命令处理器类

### Q2: 子命令不工作

**解决方案**:
- 确保父命令存在
- 检查 `parent` 参数是否匹配
- 确认父命令处理器已正确注册

### Q3: Tab Complete 不生效

**解决方案**:
- 确保实现了 `CommandCompleter` 接口
- 检查是否在 `plugin.yml` 中设置了 `tab-complete`
- 确认补全器被正确注册到命令分发器

### Q4: 权限检查不生效

**解决方案**:
- 确保方法参数包含 `Player` 或 `CommandSender`
- 检查 AspectJ 配置是否正确
- 确认 `plugin.yml` 中定义了权限节点

---

## 总结

Floyd-Core 的命令系统提供了：

- ✅ 灵活的命令注册和处理
- ✅ 强大的子命令支持
- ✅ 智能的参数补全
- ✅ 自动的权限检查
- ✅ 丰富的扩展点

合理使用命令系统可以让插件的命令更加规范、易用和可维护。