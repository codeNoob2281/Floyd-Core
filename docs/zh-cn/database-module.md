# 数据库模块文档

Floyd-Core 内置了强大的数据库管理模块，支持多种数据库类型，提供 SQL 语法构建器和数据备份功能。

## 目录

- [概述](#概述)
- [支持的数据库](#支持的数据库)
- [核心组件](#核心组件)
- [配置数据库](#配置数据库)
- [基本用法](#基本用法)
- [SQL 语法构建器](#sql-语法构建器)
- [事务管理](#事务管理)
- [数据备份](#数据备份)
- [最佳实践](#最佳实践)

---

## 概述

数据库模块提供以下功能：

- ✅ 多数据库类型支持（SQLite, MySQL, MariaDB, H2）
- ✅ 连接池管理
- ✅ SQL 语法构建器
- ✅ 自动表创建和迁移
- ✅ 数据备份和恢复
- ✅ 异步查询支持
- ✅ 预编译语句防止 SQL 注入

---

## 支持的数据库

| 数据库 | 类型 | 适用场景 |
|--------|------|----------|
| SQLite | 文件型 | 小型插件、单机服务器 |
| MySQL | 服务器型 | 大型插件、多服同步 |
| MariaDB | 服务器型 | MySQL 的替代品 |
| H2 | 内存/文件 | 测试、临时数据 |

---

## 核心组件

### DatabaseManager

数据库管理器，负责连接管理和查询执行。

```java
@Autowired
private DatabaseManager dbManager;
```

### DatabaseType

数据库类型枚举。

```java
public enum DatabaseType {
    SQLITE,
    MYSQL,
    MARIADB,
    H2
}
```

### Syntax

SQL 语法构建器，提供流式 API 构建 SQL 语句。

```java
// 构建 SELECT 语句
Syntax.select()
    .from("players")
    .where("uuid = ?", uuid)
    .build();
```

### Backup

数据备份工具，支持自动备份和手动备份。

```java
@Autowired
private Backup backupService;
```

---

## 配置数据库

### 1. 在 config.yml 中配置

```yaml
database:
  # 数据库类型: SQLITE, MYSQL, MARIADB, H2
  type: SQLITE
  
  # SQLite 配置
  sqlite:
    file: data.db  # 数据库文件路径
  
  # MySQL/MariaDB 配置
  mysql:
    host: localhost
    port: 3306
    database: myplugin
    username: root
    password: your_password
    use-ssl: false
    
    # 连接池配置
    pool:
      min-idle: 5
      max-lifetime: 1800000
      max-pool-size: 10
  
  # 通用配置
  settings:
    auto-create-tables: true   # 自动创建表
    backup-enabled: true       # 启用备份
    backup-interval: 86400     # 备份间隔（秒）
```

### 2. 初始化数据库

```java
@Component
public class DatabaseInitializer {
    
    @Autowired
    private DatabaseManager dbManager;
    
    @PostConstruct
    public void init() {
        // 初始化数据库连接
        dbManager.initialize();
        
        // 创建表
        createTables();
    }
    
    private void createTables() {
        // 创建玩家数据表
        dbManager.execute(
            "CREATE TABLE IF NOT EXISTS players (" +
            "  uuid TEXT PRIMARY KEY," +
            "  name TEXT NOT NULL," +
            "  balance INTEGER DEFAULT 0," +
            "  last_login TIMESTAMP" +
            ")"
        );
        
        // 创建物品表
        dbManager.execute(
            "CREATE TABLE IF NOT EXISTS items (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  owner_uuid TEXT," +
            "  item_data TEXT," +
            "  FOREIGN KEY (owner_uuid) REFERENCES players(uuid)" +
            ")"
        );
    }
}
```

---

## 基本用法

### 1. 执行更新操作

```java
@Service
public class PlayerDataService {
    
    @Autowired
    private DatabaseManager dbManager;
    
    /**
     * 保存玩家数据
     */
    public void savePlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        long lastLogin = System.currentTimeMillis();
        
        dbManager.execute(
            "INSERT OR REPLACE INTO players (uuid, name, last_login) VALUES (?, ?, ?)",
            uuid, name, lastLogin
        );
    }
    
    /**
     * 更新玩家余额
     */
    public void updateBalance(UUID uuid, int balance) {
        dbManager.execute(
            "UPDATE players SET balance = ? WHERE uuid = ?",
            balance, uuid.toString()
        );
    }
    
    /**
     * 删除玩家数据
     */
    public void deletePlayer(UUID uuid) {
        dbManager.execute(
            "DELETE FROM players WHERE uuid = ?",
            uuid.toString()
        );
    }
}
```

### 2. 执行查询操作

```java
/**
 * 查询单个玩家
 */
public Optional<PlayerData> getPlayer(UUID uuid) {
    ResultSet rs = dbManager.query(
        "SELECT * FROM players WHERE uuid = ?",
        uuid.toString()
    );
    
    try {
        if (rs.next()) {
            PlayerData data = new PlayerData();
            data.setUuid(UUID.fromString(rs.getString("uuid")));
            data.setName(rs.getString("name"));
            data.setBalance(rs.getInt("balance"));
            data.setLastLogin(rs.getLong("last_login"));
            return Optional.of(data);
        }
    } catch (SQLException e) {
        logger().error("查询玩家数据失败", e);
    }
    
    return Optional.empty();
}

/**
 * 查询所有玩家
 */
public List<PlayerData> getAllPlayers() {
    List<PlayerData> players = new ArrayList<>();
    
    ResultSet rs = dbManager.query("SELECT * FROM players");
    
    try {
        while (rs.next()) {
            PlayerData data = new PlayerData();
            data.setUuid(UUID.fromString(rs.getString("uuid")));
            data.setName(rs.getString("name"));
            data.setBalance(rs.getInt("balance"));
            players.add(data);
        }
    } catch (SQLException e) {
        logger().error("查询所有玩家失败", e);
    }
    
    return players;
}

/**
 * 查询余额最高的玩家
 */
public Optional<PlayerData> getRichestPlayer() {
    ResultSet rs = dbManager.query(
        "SELECT * FROM players ORDER BY balance DESC LIMIT 1"
    );
    
    try {
        if (rs.next()) {
            PlayerData data = new PlayerData();
            data.setUuid(UUID.fromString(rs.getString("uuid")));
            data.setName(rs.getString("name"));
            data.setBalance(rs.getInt("balance"));
            return Optional.of(data);
        }
    } catch (SQLException e) {
        logger().error("查询最富玩家失败", e);
    }
    
    return Optional.empty();
}
```

### 3. 批量操作

```java
/**
 * 批量保存玩家数据
 */
public void batchSavePlayers(List<Player> players) {
    String sql = "INSERT OR REPLACE INTO players (uuid, name, last_login) VALUES (?, ?, ?)";
    
    List<Object[]> batchParams = players.stream()
        .map(p -> new Object[]{
            p.getUniqueId().toString(),
            p.getName(),
            System.currentTimeMillis()
        })
        .collect(Collectors.toList());
    
    dbManager.executeBatch(sql, batchParams);
}

/**
 * 批量更新余额
 */
public void batchUpdateBalances(Map<UUID, Integer> balances) {
    String sql = "UPDATE players SET balance = ? WHERE uuid = ?";
    
    List<Object[]> batchParams = balances.entrySet().stream()
        .map(entry -> new Object[]{entry.getValue(), entry.getKey().toString()})
        .collect(Collectors.toList());
    
    dbManager.executeBatch(sql, batchParams);
}
```

### 4. 异步查询

```java
/**
 * 异步查询玩家数据
 */
public CompletableFuture<Optional<PlayerData>> getPlayerAsync(UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {
        return getPlayer(uuid);
    });
}

// 使用示例
playerDataService.getPlayerAsync(player.getUniqueId())
    .thenAccept(optional -> {
        Bukkit.getScheduler().runTask(plugin, () -> {
            optional.ifPresentOrElse(
                data -> player.sendMessage("§e你的余额: §f$" + data.getBalance()),
                () -> player.sendMessage("§c未找到你的数据")
            );
        });
    });
```

---

## SQL 语法构建器

Syntax 构建器提供类型安全的 SQL 语句构建方式。

### 1. CREATE TABLE

```java
// 基础建表
String sql = Syntax.create()
    .table("players")
    .column("uuid", "TEXT PRIMARY KEY")
    .column("name", "TEXT NOT NULL")
    .column("balance", "INTEGER DEFAULT 0")
    .column("created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    .build();

dbManager.execute(sql);

// 带外键的建表
String sql = Syntax.create()
    .table("items")
    .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
    .column("owner_uuid", "TEXT")
    .column("item_data", "TEXT NOT NULL")
    .foreignKey("owner_uuid", "players", "uuid")
    .build();

dbManager.execute(sql);
```

### 2. INSERT

```java
// 单条插入
String sql = Syntax.insert()
    .into("players")
    .columns("uuid", "name", "balance")
    .values(uuid, name, balance)
    .build();

dbManager.execute(sql);

// 多条插入
String sql = Syntax.insert()
    .into("players")
    .columns("uuid", "name", "balance")
    .values(uuid1, name1, balance1)
    .values(uuid2, name2, balance2)
    .values(uuid3, name3, balance3)
    .build();

dbManager.execute(sql);
```

### 3. SELECT

```java
// 简单查询
String sql = Syntax.select()
    .from("players")
    .build();

ResultSet rs = dbManager.query(sql);

// 带条件的查询
String sql = Syntax.select()
    .from("players")
    .where("balance > ?", minBalance)
    .andWhere("name LIKE ?", "%" + keyword + "%")
    .build();

// 指定列查询
String sql = Syntax.select()
    .columns("uuid", "name", "balance")
    .from("players")
    .where("uuid = ?", uuid)
    .build();

// 排序和限制
String sql = Syntax.select()
    .from("players")
    .orderBy("balance", Order.DESC)
    .limit(10)
    .build();

// JOIN 查询
String sql = Syntax.select()
    .columns("p.name", "COUNT(i.id) as item_count")
    .from("players p")
    .leftJoin("items i", "p.uuid = i.owner_uuid")
    .groupBy("p.uuid")
    .orderBy("item_count", Order.DESC)
    .build();
```

### 4. UPDATE

```java
// 基础更新
String sql = Syntax.update()
    .table("players")
    .set("balance", newBalance)
    .where("uuid = ?", uuid)
    .build();

dbManager.execute(sql);

// 多字段更新
String sql = Syntax.update()
    .table("players")
    .set("name", newName)
    .set("balance", newBalance)
    .set("last_login", System.currentTimeMillis())
    .where("uuid = ?", uuid)
    .build();

dbManager.execute(sql);
```

### 5. DELETE

```java
// 条件删除
String sql = Syntax.delete()
    .from("players")
    .where("uuid = ?", uuid)
    .build();

dbManager.execute(sql);

// 批量删除
String sql = Syntax.delete()
    .from("items")
    .where("owner_uuid IS NULL")
    .build();

dbManager.execute(sql);
```

### 6. ALTER TABLE

```java
// 添加列
String sql = Syntax.alter()
    .table("players")
    .add("level", "INTEGER DEFAULT 1")
    .build();

dbManager.execute(sql);

// 删除列
String sql = Syntax.alter()
    .table("players")
    .drop("old_column")
    .build();

dbManager.execute(sql);
```

---

## 事务管理

### 1. 手动事务

```java
public void transferMoney(UUID from, UUID to, int amount) {
    Connection conn = null;
    try {
        conn = dbManager.getConnection();
        conn.setAutoCommit(false);
        
        // 扣款
        PreparedStatement stmt1 = conn.prepareStatement(
            "UPDATE players SET balance = balance - ? WHERE uuid = ?"
        );
        stmt1.setInt(1, amount);
        stmt1.setString(2, from.toString());
        stmt1.executeUpdate();
        
        // 收款
        PreparedStatement stmt2 = conn.prepareStatement(
            "UPDATE players SET balance = balance + ? WHERE uuid = ?"
        );
        stmt2.setInt(1, amount);
        stmt2.setString(2, to.toString());
        stmt2.executeUpdate();
        
        // 提交事务
        conn.commit();
        
    } catch (SQLException e) {
        // 回滚事务
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger().error("事务回滚失败", ex);
            }
        }
        throw new RuntimeException("转账失败", e);
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                logger().error("关闭连接失败", e);
            }
        }
    }
}
```

### 2. 事务模板

```java
@Service
public class TransactionService {
    
    @Autowired
    private DatabaseManager dbManager;
    
    /**
     * 执行事务
     */
    public void executeInTransaction(TransactionCallback callback) {
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);
            
            callback.execute(conn);
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger().error("事务回滚失败", ex);
                }
            }
            throw new RuntimeException("事务执行失败", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger().error("关闭连接失败", e);
                }
            }
        }
    }
}

@FunctionalInterface
public interface TransactionCallback {
    void execute(Connection conn) throws SQLException;
}

// 使用示例
transactionService.executeInTransaction(conn -> {
    // 执行多个 SQL 操作
    PreparedStatement stmt1 = conn.prepareStatement(...);
    stmt1.executeUpdate();
    
    PreparedStatement stmt2 = conn.prepareStatement(...);
    stmt2.executeUpdate();
});
```

---

## 数据备份

### 1. 自动备份

在配置中启用自动备份：

```yaml
database:
  settings:
    backup-enabled: true
    backup-interval: 86400  # 每天备份一次
    backup-dir: backups     # 备份目录
    max-backups: 7          # 保留最近7个备份
```

### 2. 手动备份

```java
@Service
public class BackupService {
    
    @Autowired
    private Backup backupManager;
    
    /**
     * 创建备份
     */
    public void createBackup() {
        try {
            String backupFile = backupManager.createBackup();
            logger().info("备份创建成功: {}", backupFile);
        } catch (Exception e) {
            logger().error("备份创建失败", e);
        }
    }
    
    /**
     * 恢复备份
     */
    public void restoreBackup(String backupFile) {
        try {
            backupManager.restoreBackup(backupFile);
            logger().info("备份恢复成功: {}", backupFile);
        } catch (Exception e) {
            logger().error("备份恢复失败", e);
        }
    }
    
    /**
     * 列出所有备份
     */
    public List<String> listBackups() {
        return backupManager.listBackups();
    }
    
    /**
     * 删除旧备份
     */
    public void cleanupOldBackups(int keepCount) {
        backupManager.cleanupOldBackups(keepCount);
    }
}
```

### 3. 定时备份任务

```java
@Component
public class ScheduledBackupTask {
    
    @Autowired
    private BackupService backupService;
    
    @Scheduled(fixedRate = 86400000) // 每天执行
    public void performBackup() {
        logger().info("开始执行定时备份...");
        backupService.createBackup();
        backupService.cleanupOldBackups(7);
        logger().info("定时备份完成");
    }
}
```

---

## 最佳实践

### 1. 使用预编译语句

```java
// ❌ 不安全 - SQL 注入风险
String sql = "SELECT * FROM players WHERE name = '" + name + "'";

// ✅ 安全 - 使用预编译语句
String sql = "SELECT * FROM players WHERE name = ?";
ResultSet rs = dbManager.query(sql, name);
```

### 2. 及时关闭资源

```java
// ✅ 使用 try-with-resources
try (ResultSet rs = dbManager.query("SELECT * FROM players")) {
    while (rs.next()) {
        // 处理结果
    }
} catch (SQLException e) {
    logger().error("查询失败", e);
}
```

### 3. 添加索引优化查询

```java
// 为常用查询字段添加索引
dbManager.execute(
    "CREATE INDEX IF NOT EXISTS idx_players_balance ON players(balance)"
);

dbManager.execute(
    "CREATE INDEX IF NOT EXISTS idx_players_name ON players(name)"
);
```

### 4. 分批处理大量数据

```java
// ❌ 一次性加载所有数据
List<PlayerData> allPlayers = getAllPlayers();

// ✅ 分批处理
int pageSize = 100;
int offset = 0;
List<PlayerData> page;

do {
    page = getPlayersPage(pageSize, offset);
    processPage(page);
    offset += pageSize;
} while (!page.isEmpty());
```

### 5. 缓存频繁查询的数据

```java
@Service
public class CachedPlayerService {
    
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    
    @Autowired
    private DatabaseManager dbManager;
    
    public PlayerData getPlayer(UUID uuid) {
        // 先从缓存获取
        return cache.computeIfAbsent(uuid, this::loadFromDatabase);
    }
    
    private PlayerData loadFromDatabase(UUID uuid) {
        // 从数据库加载
        ResultSet rs = dbManager.query(
            "SELECT * FROM players WHERE uuid = ?",
            uuid.toString()
        );
        
        try {
            if (rs.next()) {
                PlayerData data = new PlayerData();
                // ... 填充数据
                return data;
            }
        } catch (SQLException e) {
            logger().error("加载玩家数据失败", e);
        }
        
        return null;
    }
    
    public void invalidateCache(UUID uuid) {
        cache.remove(uuid);
    }
}
```

### 6. 错误处理

```java
public Optional<PlayerData> getPlayer(UUID uuid) {
    try {
        ResultSet rs = dbManager.query(
            "SELECT * FROM players WHERE uuid = ?",
            uuid.toString()
        );
        
        if (rs.next()) {
            return Optional.of(mapResultSet(rs));
        }
    } catch (SQLException e) {
        logger().error("查询玩家数据失败: uuid={}", uuid, e);
        // 不要吞掉异常，记录日志
    }
    
    return Optional.empty();
}
```

### 7. 数据库迁移

```java
@Component
public class DatabaseMigration {
    
    @Autowired
    private DatabaseManager dbManager;
    
    @PostConstruct
    public void migrate() {
        int currentVersion = getCurrentVersion();
        
        if (currentVersion < 1) {
            migrateToV1();
        }
        
        if (currentVersion < 2) {
            migrateToV2();
        }
        
        updateVersion(getLatestVersion());
    }
    
    private void migrateToV1() {
        logger().info("执行数据库迁移: v0 -> v1");
        dbManager.execute(
            "ALTER TABLE players ADD COLUMN level INTEGER DEFAULT 1"
        );
    }
    
    private void migrateToV2() {
        logger().info("执行数据库迁移: v1 -> v2");
        dbManager.execute(
            "CREATE TABLE IF NOT EXISTS achievements (" +
            "  player_uuid TEXT," +
            "  achievement_id TEXT," +
            "  unlocked_at TIMESTAMP," +
            "  PRIMARY KEY (player_uuid, achievement_id)" +
            ")"
        );
    }
    
    private int getCurrentVersion() {
        // 从配置文件或数据库读取当前版本
        return 0;
    }
    
    private void updateVersion(int version) {
        // 更新版本号
    }
}
```

### 8. 连接池配置

```yaml
database:
  mysql:
    pool:
      min-idle: 5              # 最小空闲连接
      max-pool-size: 10        # 最大连接数
      connection-timeout: 30000 # 连接超时时间（毫秒）
      max-lifetime: 1800000    # 连接最大生命周期（30分钟）
      idle-timeout: 600000     # 空闲连接超时（10分钟）
```

---

## 完整示例

```java
@Service
public class CompleteExampleService {
    
    @Autowired
    private DatabaseManager dbManager;
    
    @Autowired
    private BackupService backupService;
    
    /**
     * 初始化数据库
     */
    @PostConstruct
    public void init() {
        createTables();
        createIndexes();
    }
    
    private void createTables() {
        // 使用 Syntax 构建器创建表
        String playersTable = Syntax.create()
            .table("players")
            .column("uuid", "TEXT PRIMARY KEY")
            .column("name", "TEXT NOT NULL")
            .column("balance", "INTEGER DEFAULT 0")
            .column("level", "INTEGER DEFAULT 1")
            .column("created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
            .build();
        
        dbManager.execute(playersTable);
        
        String itemsTable = Syntax.create()
            .table("items")
            .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
            .column("owner_uuid", "TEXT")
            .column("item_data", "TEXT NOT NULL")
            .column("created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
            .foreignKey("owner_uuid", "players", "uuid")
            .build();
        
        dbManager.execute(itemsTable);
    }
    
    private void createIndexes() {
        dbManager.execute("CREATE INDEX IF NOT EXISTS idx_players_balance ON players(balance)");
        dbManager.execute("CREATE INDEX IF NOT EXISTS idx_items_owner ON items(owner_uuid)");
    }
    
    /**
     * 保存玩家数据（带事务）
     */
    public void savePlayerData(UUID uuid, String name, int balance) {
        try {
            String sql = Syntax.insert()
                .into("players")
                .columns("uuid", "name", "balance")
                .values(uuid.toString(), name, balance)
                .build();
            
            dbManager.execute(sql, uuid.toString(), name, balance);
        } catch (Exception e) {
            logger().error("保存玩家数据失败", e);
            throw new RuntimeException("保存失败", e);
        }
    }
    
    /**
     * 查询玩家排行榜
     */
    public List<PlayerData> getLeaderboard(int limit) {
        String sql = Syntax.select()
            .columns("uuid", "name", "balance")
            .from("players")
            .orderBy("balance", Order.DESC)
            .limit(limit)
            .build();
        
        List<PlayerData> result = new ArrayList<>();
        
        try (ResultSet rs = dbManager.query(sql)) {
            while (rs.next()) {
                PlayerData data = new PlayerData();
                data.setUuid(UUID.fromString(rs.getString("uuid")));
                data.setName(rs.getString("name"));
                data.setBalance(rs.getInt("balance"));
                result.add(data);
            }
        } catch (SQLException e) {
            logger().error("查询排行榜失败", e);
        }
        
        return result;
    }
    
    /**
     * 转账（带事务）
     */
    @Transactional
    public void transferMoney(UUID from, UUID to, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("转账金额必须大于0");
        }
        
        // 检查余额
        PlayerData sender = getPlayer(from)
            .orElseThrow(() -> new RuntimeException("发送者不存在"));
        
        if (sender.getBalance() < amount) {
            throw new RuntimeException("余额不足");
        }
        
        // 执行转账
        dbManager.execute(
            "UPDATE players SET balance = balance - ? WHERE uuid = ?",
            amount, from.toString()
        );
        
        dbManager.execute(
            "UPDATE players SET balance = balance + ? WHERE uuid = ?",
            amount, to.toString()
        );
        
        logger().info("转账成功: {} -> {} 金额: {}", from, to, amount);
    }
}
```

---

## 常见问题

### Q1: 数据库连接失败

**解决方案**:
- 检查数据库配置是否正确
- 确认数据库服务正在运行
- 验证用户名和密码
- 检查防火墙设置

### Q2: SQL 语法错误

**解决方案**:
- 使用 Syntax 构建器避免手写 SQL
- 检查 SQLite 和 MySQL 的语法差异
- 使用预编译语句而不是字符串拼接

### Q3: 性能问题

**解决方案**:
- 为常用查询字段添加索引
- 使用连接池管理连接
- 缓存频繁访问的数据
- 使用异步查询处理耗时操作

### Q4: 数据丢失

**解决方案**:
- 启用自动备份
- 定期手动备份
- 使用事务保证数据一致性
- 在执行危险操作前先备份

---

## 总结

Floyd-Core 的数据库模块提供了：

- ✅ 多数据库支持
- ✅ 类型安全的 SQL 构建
- ✅ 事务管理
- ✅ 自动备份
- ✅ 连接池优化

合理使用数据库模块可以让数据存储更加安全、高效和可靠。