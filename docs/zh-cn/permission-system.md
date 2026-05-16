# 权限系统文档

Floyd-Core 的权限系统基于 AOP（面向切面编程）实现，通过注解方式进行权限检查，无需手动编写权限验证代码。

## 目录

- [概述](#概述)
- [核心组件](#核心组件)
- [基本用法](#基本用法)
- [高级特性](#高级特性)
- [权限组管理](#权限组管理)
- [最佳实践](#最佳实践)

---

## 概述

权限系统提供以下功能：

- ✅ 基于注解的权限检查
- ✅ AOP 无侵入式设计
- ✅ 自动玩家参数检测
- ✅ 自定义错误消息
- ✅ 权限值提示
- ✅ 灵活的权限节点设计
- ✅ 与命令系统无缝集成

---

## 核心组件

### @RequiredPermission

权限检查注解，用于标记需要权限验证的方法。

```java
@RequiredPermission(
    value = "permission.node",      // 权限节点（必填）
    message = "自定义错误消息",      // 错误消息（可选）
    tipPermValue = false            // 是否显示权限值（可选）
)
```

### PermissionAspect

权限切面实现，负责拦截带有 `@RequiredPermission` 注解的方法并执行权限检查。

### PermissionUtil

权限工具类，提供权限检查的辅助方法。

```java
@Autowired
private PermissionUtil permissionUtil;
```

---

## 基本用法

### 1. 基础权限检查

```java
@Service
public class BackpackService {
    
    /**
     * 打开背包 - 需要基础权限
     */
    @RequiredPermission("myplugin.backpack.open")
    public void openBackpack(Player player, int backpackId) {
        // 只有拥有 myplugin.backpack.open 权限的玩家才能执行
        // 权限不足时自动发送错误消息并返回
        logger().info("玩家 {} 打开背包 #{}", player.getName(), backpackId);
    }
}
```

### 2. 自定义错误消息

```java
@Service
public class AdminService {
    
    /**
     * 重载配置 - 自定义错误消息
     */
    @RequiredPermission(
        value = "myplugin.admin.reload",
        message = "§c你没有权限重载配置！请联系管理员。"
    )
    public void reloadConfig(Player player) {
        // 执行业务逻辑
        logger().info("管理员 {} 重载了配置", player.getName());
    }
}
```

### 3. 显示权限值

```java
@Service
public class GuildService {
    
    /**
     * 创建公会 - 显示所需权限
     */
    @RequiredPermission(
        value = "myplugin.guild.create",
        tipPermValue = true
    )
    public void createGuild(Player player, String guildName) {
        // 权限不足时显示: "你没有权限! 需要: myplugin.guild.create"
        logger().info("玩家 {} 创建了公会 {}", player.getName(), guildName);
    }
}
```

### 4. 组合使用

```java
@Service
public class EconomyService {
    
    /**
     * 大额转账 - 完整配置
     */
    @RequiredPermission(
        value = "myplugin.economy.transfer.large",
        message = "§c你需要特殊权限才能进行大额转账！",
        tipPermValue = true
    )
    public void largeTransfer(Player player, UUID target, int amount) {
        // 需要 myplugin.economy.transfer.large 权限
        // 自定义错误消息
        // 显示所需权限值
        logger().info("玩家 {} 转账 {} 给 {}", player.getName(), amount, target);
    }
}
```

---

## 高级特性

### 1. 多级权限检查

```java
@Service
public class MultiLevelPermissionService {
    
    /**
     * 基础操作
     */
    @RequiredPermission("myplugin.item.use")
    public void useItem(Player player, ItemStack item) {
        // 需要 myplugin.item.use 权限
    }
    
    /**
     * 高级操作
     */
    @RequiredPermission("myplugin.item.use.premium")
    public void usePremiumItem(Player player, ItemStack item) {
        // 需要 myplugin.item.use.premium 权限
        // 通常这个权限包含基础权限
    }
    
    /**
     * 管理员操作
     */
    @RequiredPermission("myplugin.item.admin.give")
    public void giveItem(Player player, Player target, ItemStack item) {
        // 需要 myplugin.item.admin.give 权限
    }
}
```

### 2. 动态权限检查

在方法内部进行额外的权限验证：

```java
@Service
public class DynamicPermissionService {
    
    @Autowired
    private PermissionUtil permissionUtil;
    
    @RequiredPermission("myplugin.bank.withdraw")
    public void withdraw(Player player, int amount) {
        // 首先通过注解检查基础权限
        
        // 然后根据金额进行额外检查
        if (amount > 10000 && !player.hasPermission("myplugin.bank.withdraw.unlimited")) {
            player.sendMessage("§c单次最多提取 $10,000");
            return;
        }
        
        // 执行提款逻辑
        logger().info("玩家 {} 提取了 ${}", player.getName(), amount);
    }
}
```

### 3. 权限继承

通过在权限插件中配置权限继承：

```yaml
# LuckPerms 配置示例
groups:
  vip:
    permissions:
      - myplugin.backpack.open
      - myplugin.backpack.upgrade
  
  admin:
    inheritance:
      - vip
    permissions:
      - myplugin.admin.*
      - myplugin.*
```

### 4. 通配符权限

```java
@Service
public class WildcardPermissionService {
    
    /**
     * 使用通配符权限
     */
    @RequiredPermission("myplugin.commands.*")
    public void executeCommand(Player player, String command) {
        // 拥有 myplugin.commands.any 或 myplugin.commands.* 的玩家都可以执行
    }
}
```

在权限插件中配置：

```yaml
permissions:
  myplugin.commands.*:
    description: "允许执行所有命令"
    children:
      myplugin.commands.teleport: true
      myplugin.commands.heal: true
      myplugin.commands.item: true
```

### 5. 条件权限检查

```java
@Service
public class ConditionalPermissionService {
    
    @RequiredPermission("myplugin.warp.use")
    public void teleportToWarp(Player player, String warpName) {
        // 检查是否是付费传送点
        if (isPremiumWarp(warpName)) {
            // 需要额外权限
            if (!player.hasPermission("myplugin.warp.premium")) {
                player.sendMessage("§c这是付费传送点，需要 VIP 权限！");
                return;
            }
        }
        
        // 执行传送
        Warp warp = getWarp(warpName);
        player.teleport(warp.getLocation());
    }
    
    private boolean isPremiumWarp(String warpName) {
        // 检查是否是付费传送点
        return premiumWarps.contains(warpName);
    }
}
```

### 6. 权限缓存优化

```java
@Service
public class CachedPermissionService {
    
    private final Map<UUID, Set<String>> permissionCache = new ConcurrentHashMap<>();
    private final long cacheTimeout = 60000; // 1分钟
    
    @Autowired
    private PermissionUtil permissionUtil;
    
    /**
     * 检查权限（带缓存）
     */
    public boolean hasPermissionCached(Player player, String permission) {
        UUID playerId = player.getUniqueId();
        Set<String> perms = permissionCache.computeIfAbsent(playerId, 
            k -> new HashSet<>());
        
        if (perms.contains(permission)) {
            return true;
        }
        
        boolean hasPerm = player.hasPermission(permission);
        if (hasPerm) {
            perms.add(permission);
        }
        
        return hasPerm;
    }
    
    /**
     * 清除缓存
     */
    public void clearCache(UUID playerId) {
        permissionCache.remove(playerId);
    }
}
```

---

## 权限组管理

### 1. 定义权限层级

```
myplugin/
├── user/
│   ├── backpack.open          # 打开背包
│   ├── backpack.close         # 关闭背包
│   └── warp.use               # 使用传送点
├── vip/
│   ├── backpack.upgrade       # 升级背包
│   ├── warp.premium           # 使用付费传送点
│   └── item.enchant           # 物品附魔
├── admin/
│   ├── reload                 # 重载配置
│   ├── player.kick            # 踢出玩家
│   └── player.ban             # 封禁玩家
└── *                          # 所有权限
```

### 2. 权限配置文件

**plugin.yml**
```yaml
permissions:
  myplugin.user.*:
    description: "MyPlugin 用户权限"
    default: true
    children:
      myplugin.user.backpack.open: true
      myplugin.user.backpack.close: true
      myplugin.user.warp.use: true
  
  myplugin.vip.*:
    description: "MyPlugin VIP 权限"
    default: false
    children:
      myplugin.user.*: true
      myplugin.vip.backpack.upgrade: true
      myplugin.vip.warp.premium: true
      myplugin.vip.item.enchant: true
  
  myplugin.admin.*:
    description: "MyPlugin 管理员权限"
    default: op
    children:
      myplugin.vip.*: true
      myplugin.admin.reload: true
      myplugin.admin.player.kick: true
      myplugin.admin.player.ban: true
```

### 3. 使用 LuckPerms 管理权限

```bash
# 创建用户组
lp creategroup vip
lp creategroup admin

# 设置权限
lp group vip permission set myplugin.vip.* true
lp group admin permission set myplugin.admin.* true

# 设置继承
lp group admin parent add vip

# 添加玩家到组
lp user Steve parent add vip
lp user Alex parent add admin
```

### 4. 权限检查工具类

```java
@Component
public class PermissionChecker {
    
    @Autowired
    private I18nMessageProvider i18n;
    
    /**
     * 检查玩家是否有任意一个权限
     */
    public boolean hasAnyPermission(Player player, String... permissions) {
        for (String perm : permissions) {
            if (player.hasPermission(perm)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查玩家是否有所有权限
     */
    public boolean hasAllPermissions(Player player, String... permissions) {
        for (String perm : permissions) {
            if (!player.hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取玩家的最高权限等级
     */
    public int getPermissionLevel(Player player) {
        if (player.hasPermission("myplugin.admin.*")) {
            return 3;
        } else if (player.hasPermission("myplugin.vip.*")) {
            return 2;
        } else if (player.hasPermission("myplugin.user.*")) {
            return 1;
        }
        return 0;
    }
}
```

---

## 与命令系统集成

### 1. 命令级权限

```java
@Component
public class CommandHandler {
    
    @CommandHandler("admin")
    @RequiredPermission("myplugin.admin.use")
    public void handleAdmin(Player player, String[] args) {
        player.sendMessage("§c管理员面板");
    }
}
```

### 2. 子命令权限

```java
@Component
public class BackpackCommandHandler {
    
    @SubCommandHandler(parent = "backpack", name = "open")
    @RequiredPermission("myplugin.backpack.open")
    public void openBackpack(Player player, String[] args) {
        // 打开背包
    }
    
    @SubCommandHandler(parent = "backpack", name = "upgrade")
    @RequiredPermission("myplugin.backpack.upgrade")
    public void upgradeBackpack(Player player, String[] args) {
        // 升级背包
    }
    
    @SubCommandHandler(parent = "backpack", name = "clear")
    @RequiredPermission("myplugin.admin.backpack.clear")
    public void clearBackpack(Player player, String[] args) {
        // 清理背包（管理员）
    }
}
```

### 3. 动态权限补全

```java
@Component
public class PermissionAwareCompleter implements CommandCompleter {
    
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // 根据权限返回不同的补全选项
        if (sender.hasPermission("myplugin.admin")) {
            completions.addAll(Arrays.asList("reload", "debug", "stats"));
        }
        
        if (sender.hasPermission("myplugin.user")) {
            completions.addAll(Arrays.asList("info", "help"));
        }
        
        return completions;
    }
}
```

---

## 最佳实践

### 1. 权限命名规范

```java
// ✅ 好的命名
@RequiredPermission("myplugin.module.action")
@RequiredPermission("myplugin.backpack.open")
@RequiredPermission("myplugin.admin.player.kick")

// ❌ 避免的命名
@RequiredPermission("open")
@RequiredPermission("myplugin_open_backpack")
@RequiredPermission("admin123")
```

**命名规则**:
- 格式: `插件名.模块.操作`
- 使用小写字母和点号分隔
- 层次清晰，便于管理
- 支持通配符: `myplugin.admin.*`

### 2. 默认权限设置

```yaml
permissions:
  myplugin.user.basic:
    description: "基础功能"
    default: true        # 所有玩家默认拥有
  
  myplugin.vip.extra:
    description: "VIP 功能"
    default: false       # 需要手动分配
  
  myplugin.admin.all:
    description: "管理员功能"
    default: op          # OP 玩家默认拥有
```

### 3. 友好的错误提示

```java
// ❌ 不好的做法
@RequiredPermission("myplugin.admin")
// 默认消息: "you don't have permission..."

// ✅ 好的做法
@RequiredPermission(
    value = "myplugin.admin.reload",
    message = "§c你没有权限重载配置！\n§7需要权限: myplugin.admin.reload"
)
```

### 4. 权限分组管理

```java
public final class PermissionGroups {
    
    // 用户权限
    public static final String USER_BACKPACK_OPEN = "myplugin.user.backpack.open";
    public static final String USER_WARP_USE = "myplugin.user.warp.use";
    
    // VIP 权限
    public static final String VIP_BACKPACK_UPGRADE = "myplugin.vip.backpack.upgrade";
    public static final String VIP_WARP_PREMIUM = "myplugin.vip.warp.premium";
    
    // 管理员权限
    public static final String ADMIN_RELOAD = "myplugin.admin.reload";
    public static final String ADMIN_KICK = "myplugin.admin.player.kick";
    
    private PermissionGroups() {
        // 防止实例化
    }
}

// 使用常量
@RequiredPermission(PermissionGroups.ADMIN_RELOAD)
public void reloadConfig(Player player) {
    // ...
}
```

### 5. 权限检查日志

```java
@Aspect
@Component
public class PermissionLoggingAspect {
    
    @Autowired
    private Logger logger;
    
    @Before("@annotation(requiredPermission)")
    public void logPermissionCheck(JoinPoint joinPoint, RequiredPermission requiredPermission) {
        Object[] args = joinPoint.getArgs();
        
        for (Object arg : args) {
            if (arg instanceof Player) {
                Player player = (Player) arg;
                String methodName = joinPoint.getSignature().getName();
                
                logger.debug("权限检查: 玩家={}, 方法={}, 权限={}", 
                           player.getName(), methodName, requiredPermission.value());
                break;
            }
        }
    }
}
```

### 6. 避免过度使用权限检查

```java
// ❌ 每个方法都检查
@RequiredPermission("myplugin.item.use")
public void useItem(Player player) { ... }

@RequiredPermission("myplugin.item.drop")
public void dropItem(Player player) { ... }

@RequiredPermission("myplugin.item.pickup")
public void pickupItem(Player player) { ... }

// ✅ 合理分组
@RequiredPermission("myplugin.item.basic")
public void useItem(Player player) { ... }

public void dropItem(Player player) { ... }  // 不需要额外权限

public void pickupItem(Player player) { ... }  // 不需要额外权限
```

### 7. 权限版本管理

```java
/**
 * 权限迁移
 */
@Component
public class PermissionMigration {
    
    @PostConstruct
    public void migratePermissions() {
        // 旧版本权限 -> 新版本权限
        Map<String, String> migrationMap = new HashMap<>();
        migrationMap.put("myplugin.old.perm", "myplugin.new.perm");
        migrationMap.put("myplugin.backpack.access", "myplugin.backpack.open");
        
        logger().info("权限迁移映射已加载，共 {} 条规则", migrationMap.size());
    }
}
```

### 8. 测试权限配置

```java
@SpringBootTest
public class PermissionTest {
    
    @Autowired
    private Plugin plugin;
    
    @Test
    public void testPermissionHierarchy() {
        // 创建测试玩家
        Player player = mock(Player.class);
        
        // 测试权限继承
        when(player.hasPermission("myplugin.admin.*")).thenReturn(true);
        
        assertTrue(player.hasPermission("myplugin.admin.reload"));
        assertTrue(player.hasPermission("myplugin.admin.kick"));
    }
    
    @Test
    public void testDefaultPermissions() {
        Player player = mock(Player.class);
        
        // 测试默认权限
        when(player.hasPermission("myplugin.user.basic")).thenReturn(true);
        
        assertTrue(player.hasPermission("myplugin.user.basic"));
    }
}
```

---

## 完整示例

```java
@Service
public class CompletePermissionExample {
    
    @Autowired
    private I18nMessageProvider i18n;
    
    @Autowired
    private DatabaseManager db;
    
    /**
     * 基础功能 - 所有玩家可用
     */
    @RequiredPermission("myplugin.balance.check")
    public void checkBalance(Player player) {
        int balance = getBalance(player);
        i18n.sendMessage(player, "economy.balance", balance);
    }
    
    /**
     * VIP 功能 - 需要 VIP 权限
     */
    @RequiredPermission(
        value = "myplugin.balance.deposit",
        message = "§c只有 VIP 玩家才能存款！"
    )
    public void deposit(Player player, int amount) {
        if (amount <= 0) {
            player.sendMessage("§c存款金额必须大于 0");
            return;
        }
        
        updateBalance(player, amount);
        i18n.sendMessage(player, "economy.deposit-success", amount);
    }
    
    /**
     * 管理员功能 - 完整配置
     */
    @RequiredPermission(
        value = "myplugin.admin.set-balance",
        message = "§c你没有权限修改其他玩家的余额！",
        tipPermValue = true
    )
    public void setBalance(Player admin, Player target, int amount) {
        if (amount < 0) {
            admin.sendMessage("§c余额不能为负数");
            return;
        }
        
        setPlayerBalance(target, amount);
        
        i18n.sendMessage(admin, "admin.set-balance-success", 
                        target.getName(), amount);
        i18n.sendMessage(target, "balance-changed-by-admin", 
                        admin.getName(), amount);
        
        logger().info("管理员 {} 设置玩家 {} 的余额为 {}", 
                     admin.getName(), target.getName(), amount);
    }
    
    /**
     * 动态权限检查示例
     */
    @RequiredPermission("myplugin.shop.buy")
    public void buyItem(Player player, String itemId, int quantity) {
        ShopItem item = getShopItem(itemId);
        
        if (item == null) {
            player.sendMessage("§c商品不存在");
            return;
        }
        
        int totalPrice = item.getPrice() * quantity;
        int balance = getBalance(player);
        
        // 动态权限检查：大额购买需要额外权限
        if (totalPrice > 100000 && !player.hasPermission("myplugin.shop.buy.large")) {
            player.sendMessage("§c单笔交易不能超过 $100,000");
            return;
        }
        
        if (balance < totalPrice) {
            i18n.sendMessage(player, "economy.insufficient", balance);
            return;
        }
        
        // 执行购买
        deductBalance(player, totalPrice);
        giveItem(player, item, quantity);
        
        i18n.sendMessage(player, "shop.purchase-success", 
                        item.getName(), quantity, totalPrice);
    }
}
```

---

## 常见问题

### Q1: 权限检查不生效

**解决方案**:
- 确保方法参数包含 `Player` 或 `CommandSender`
- 检查 AspectJ 是否正确配置
- 确认 `@RequiredPermission` 注解在 Spring Bean 方法上
- 验证权限插件是否正确配置

### Q2: 权限消息没有显示

**解决方案**:
- 检查自定义消息是否为空
- 确认玩家在线状态
- 验证消息发送逻辑

### Q3: 性能问题

**解决方案**:
- 避免在高频调用的方法中使用权限检查
- 使用权限缓存减少重复检查
- 合理使用权限层级和通配符

### Q4: 权限冲突

**解决方案**:
- 检查权限插件中的权限覆盖设置
- 使用明确的权限节点而不是通配符
- 定期审查和清理未使用的权限

---

## 总结

Floyd-Core 的权限系统提供了：

- ✅ 简洁的注解式权限检查
- ✅ 无侵入的 AOP 实现
- ✅ 灵活的权限配置
- ✅ 与命令系统完美集成
- ✅ 丰富的扩展性

合理使用权限系统可以让插件的权限管理更加规范、安全和易维护。