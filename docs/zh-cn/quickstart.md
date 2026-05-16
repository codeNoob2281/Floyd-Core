# 快速开始指南

本指南将帮助你快速上手 Floyd-Core 框架，创建你的第一个 Minecraft 插件。

## 环境准备

### 必需软件

- **JDK 21** 或更高版本
- **Maven 3.6+**
- **IDE**: IntelliJ IDEA（推荐）或 Eclipse
- **服务器**: PaperMC 1.21.11+

### 验证安装

```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version
```

## 步骤 1: 创建 Maven 项目

### 1.1 初始化项目

使用 Maven  archetype 或手动创建项目结构：

```
my-plugin/
├── src/main/java/
│   └── com/example/myplugin/
├── src/main/resources/
└── pom.xml
```

### 1.2 配置 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <repositories>
        <!-- Floyd-Core 仓库 -->
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/codeNoob2281/Floyd-Core</url>
        </repository>
        
        <!-- PaperMC 仓库 -->
        <repository>
            <id>papermc</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <!-- Floyd-Core 框架 -->
        <dependency>
            <groupId>com.codefish.mc</groupId>
            <artifactId>floyd-core</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        
        <!-- Lombok (可选但推荐) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
            
            <!-- Maven Shade Plugin - 打包依赖 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <relocations>
                                <!-- 重定位 Spring 避免冲突 -->
                                <relocation>
                                    <pattern>org.springframework</pattern>
                                    <shadedPattern>com.example.myplugin.lib.spring</shadedPattern>
                                </relocation>
                            </relocations>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## 步骤 2: 创建插件主类

### 2.1 编写主类

```java
package com.example.myplugin;

import com.floyd.core.FloydPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        // 插件启用时的初始化逻辑
        logger().info("=================================");
        logger().info("  MyPlugin 已成功启用!");
        logger().info("  版本: " + getPluginMeta().getVersion());
        logger().info("=================================");
    }
    
    @Override
    protected void cleanup() {
        // 插件禁用时的清理逻辑
        logger().info("MyPlugin 已禁用");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        // 返回 Spring 配置类
        return new Class[]{PluginConfig.class};
    }
}
```

### 2.2 创建 Spring 配置类

```java
package com.example.myplugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.myplugin")
public class PluginConfig {
    
    @Bean
    public CommandHandler commandHandler() {
        return new CommandHandler();
    }
    
    @Bean
    public PlayerService playerService() {
        return new PlayerService();
    }
}
```

## 步骤 3: 创建 plugin.yml

在 `src/main/resources/` 目录下创建 `plugin.yml`:

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.myplugin.MyPlugin
api-version: '1.21'
authors:
  - YourName
description: My first Floyd-Core plugin
website: https://example.com

commands:
  myplugin:
    description: Main command for MyPlugin
    usage: /<command> [subcommand]
    aliases: [mp]
    permission: myplugin.use

permissions:
  myplugin.use:
    description: Allows using MyPlugin commands
    default: true
  myplugin.admin:
    description: Admin permissions for MyPlugin
    default: op
```

## 步骤 4: 实现业务逻辑

### 4.1 创建命令处理器

```java
package com.example.myplugin;

import com.floyd.core.permission.RequiredPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Service;

@Service
public class CommandHandler {
    
    /**
     * 基础命令 - 所有玩家可用
     */
    @RequiredPermission(value = "myplugin.use", message = "你没有权限使用此命令!")
    public void handleInfo(CommandSender sender) {
        sender.sendMessage("§6§l=== MyPlugin ===");
        sender.sendMessage("§e版本: §f1.0.0");
        sender.sendMessage("§e作者: §fYourName");
    }
    
    /**
     * 管理员命令 - 需要 admin 权限
     */
    @RequiredPermission(value = "myplugin.admin", message = "只有管理员才能执行此命令!")
    public void handleReload(Player player) {
        // 重新加载配置逻辑
        player.sendMessage("§a配置已重新加载!");
    }
}
```

### 4.2 创建服务类

```java
package com.example.myplugin;

import org.bukkit.entity.Player;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    
    public void welcomePlayer(Player player) {
        player.sendMessage("§6欢迎, §e" + player.getName() + "§6!");
        player.sendMessage("§7输入 §f/mp §7查看插件信息");
    }
    
    public boolean isValidPlayer(Player player) {
        return player != null && player.isOnline();
    }
}
```

### 4.3 注册事件监听器

```java
package com.example.myplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerListener implements Listener {
    
    @Autowired
    private PlayerService playerService;
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerService.welcomePlayer(event.getPlayer());
    }
}
```

## 步骤 5: 编译和测试

### 5.1 编译项目

```bash
mvn clean package
```

编译成功后，在 `target/` 目录下会生成：
- `my-plugin-1.0.0.jar` - 包含所有依赖的完整 JAR

### 5.2 部署到服务器

1. 将生成的 JAR 文件复制到 PaperMC 服务器的 `plugins/` 目录
2. 启动或重启服务器
3. 检查控制台输出确认插件加载成功

### 5.3 测试插件

在游戏中执行：
```
/mp info          # 查看插件信息
/mp reload        # 重新加载配置（需要管理员权限）
```

## 常见问题

### Q1: 找不到 Floyd-Core 依赖

**解决方案**: 确保在 `pom.xml` 中正确配置了 GitHub Packages 仓库，并设置了认证信息。

在 `~/.m2/settings.xml` 中添加：

```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
</servers>
```

### Q2: Spring Bean 注入失败

**解决方案**: 
- 确保配置类上有 `@ComponentScan` 注解
- 检查包扫描路径是否正确
- 确认 Bean 类上有适当的注解（`@Service`, `@Component` 等）

### Q3: 权限检查不生效

**解决方案**:
- 确保方法参数中包含 `Player` 或 `CommandSender` 类型
- 检查 AspectJ 是否正确配置
- 确认 `plugin.yml` 中定义了相应的权限节点

### Q4: 日志没有输出到文件

**解决方案**:
在 `config.yml` 中启用文件日志：

```yaml
logging:
  file:
    enable: true
```

## 下一步

恭喜你完成了第一个 Floyd-Core 插件！接下来可以探索：

- 📖 [核心模块文档](core-modules.md) - 深入了解各功能模块
- 🔧 [命令系统](command-system.md) - 学习高级命令处理
- 🗄️ [数据库模块](database-module.md) - 集成数据存储
- 🌍 [国际化](i18n-module.md) - 支持多语言
- ⚙️ [配置管理](configuration.md) - 高级配置技巧

## 获取帮助

- 📝 查看 [API 参考文档](api-reference.md)
- 🐛 报告问题: [GitHub Issues](https://github.com/codeNoob2281/Floyd-Core/issues)
- 💬 社区讨论: [GitHub Discussions](https://github.com/codeNoob2281/Floyd-Core/discussions)