# Floyd-Core

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![PaperMC](https://img.shields.io/badge/PaperMC-1.21.11-green.svg)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

---

**🌐 Language / 语言**: 
[English](README.md) | [简体中文](README_zh.md)

---

Floyd-Core is a lightweight Minecraft plugin development framework built on Spring Framework and PaperMC API, designed to simplify plugin development with modern Java practices.

## ✨ Features

- 🍃 **Lightweight Design**: Clean and intuitive API for easy learning curve
- 🌱 **Spring Integration**: Built-in Spring Framework support for dependency injection and bean management
- 🔒 **Permission Annotations**: AOP-based permission checking for simplified validation logic
- 📦 **Item Serialization**: ItemStack serialization/deserialization utilities included
- 📝 **Logging System**: Dual console and file logging support out of the box
- 🔧 **Highly Customizable**: Support for custom banners, configurations, and lifecycle management

## 📋 Requirements

- **Java**: 21 or higher
- **Maven**: 3.6 or higher
- **Server**: PaperMC 1.21.11 or higher

## 📦 Dependencies

- Spring Framework 6.2.7
- Lombok 1.18.30
- PaperMC API 1.21.11
- AspectJ 1.9.7

## 🚀 Quick Start

### 1. Maven Setup

First, add the GitHub Packages repository to your `pom.xml`:

```xml
<repositories>
    <!-- ... other repositories ... -->
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/codeNoob2281/Floyd-Core</url>
    </repository>
    <!-- ... other repositories ... -->
</repositories>
```

Then add the dependency:

```xml
<dependency>
    <groupId>com.codefish.mc</groupId>
    <artifactId>floyd-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Create Your Plugin

Extend the `FloydPlugin` class and implement the abstract methods:

```java
public class MyPlugin extends FloydPlugin {
    
    @Override
    public String getPluginName() {
        return "MyPlugin";
    }
    
    @Override
    protected void initialize() {
        // Initialization logic when plugin is enabled
        getLogger().info("Plugin is enabled!");
    }
    
    @Override
    protected void cleanup() {
        // Cleanup logic when plugin is disabled
        getLogger().info("Plugin is disabled!");
    }
    
    @Override
    protected Class<?>[] getConfigClasses() {
        // Return Spring configuration classes array
        return new Class[]{MyConfig.class};
    }
}
```

Register your plugin in `plugin.yml`:

```yaml
name: MyPlugin
version: 1.0.0
main: com.yourpackage.MyPlugin
api-version: '1.21'
authors:
  - YourName
```

### 3. Spring Configuration

Create your Spring configuration class:

```java
@Configuration
@ComponentScan("com.yourpackage")
public class MyConfig {
    
    @Bean
    public MyService myService() {
        return new MyService();
    }
    
    @Bean
    public CommandExecutor myCommandExecutor() {
        return new MyCommandExecutor();
    }
}
```

Inject dependencies using Spring's `@Autowired`:

```java
@Service
public class MyService {
    
    @Autowired
    private FloydPlugin plugin;
    
    public void doSomething() {
        // Use plugin instance
    }
}
```

### 4. Logging System

Use the built-in logger with multiple log levels:

```java
// Get logger instance via logger() method
logger().info("This is an informational message");
logger().debug("Debug information for development");
logger().warning("Warning about potential issues");
logger().error("Error occurred during execution");
logger().error("Exception details", exception);
```

Configure file logging in `config.yml`:

```yaml
logging:
  file:
    enable: true  # Enable file logging
```

Logs are written to both console and `{dataFolder}/mc-plugin.log` by default.

### 5. Permission Annotations (AOP)

Automate permission checks using the `@RequiredPermission` annotation:

```java
@Service
public class BackpackService {
    
    @RequiredPermission(value = "floyd-backpack.open", message = "You don't have permission to open backpack!")
    public void openBackpack(Player player, int backpackId) {
        // Automatically validates if player has floyd-backpack.open permission
        // Method won't execute if permission check fails
        // Opens backpack logic here
    }
    
    @RequiredPermission(value = "floyd-backpack.upgrade", tipPermValue = true)
    public void upgradeBackpack(Player player, int level) {
        // Shows required permission value in error message
        // Upgrade backpack logic here
    }
}
```

#### Features

- ✅ Automatic Player parameter detection and permission validation
- ✅ Red warning messages sent automatically on insufficient permission
- ✅ No manual permission check code needed
- ✅ Non-invasive design powered by AspectJ

#### Annotation Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | String | - | Permission identifier (required) |
| `message` | String | `"you don't have permission..."` | Custom error message |
| `tipPermValue` | boolean | `false` | Whether to show permission value in message |

#### Best Practices

- Method must include a `Player` type parameter
- Permission format recommendation: `plugin-name.module.operation`
- Example: `floyd-backpack.admin.upgrade`

### 6. Item Serialization

Serialize and deserialize ItemStacks easily:

```java
import com.floyd.core.inventory.io.ItemStackSerializer;
import com.floyd.core.inventory.io.BukkitItemStackSerializer;

// Initialize serializer
ItemStackSerializer serializer = new BukkitItemStackSerializer();

// Serialize ItemStack to Base64 string
ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
String serialized = serializer.serialize(itemStack);

// Deserialize back to ItemStack
ItemStack restored = serializer.deserialize(serialized);
```

The serialization uses Bukkit's official `serializeAsBytes()` method and Base64 encoding for safe storage in databases or config files.

## 🏗️ Core Modules

### FloydPlugin (Base Class)

The main plugin base class providing:

- Spring ApplicationContext integration
- Custom banner support
- Automatic configuration saving
- Logging system initialization
- Plugin lifecycle management (onEnable/onDisable)

### Logging Module

| Class | Description |
|-------|-------------|
| `ConsoleLogger` | Logging interface with debug/info/warning/error levels |
| `DefaultConsoleLogger` | Implementation supporting dual console and file output |
| `LogConfig` | Configuration class for logging behavior |

### Inventory IO Module

| Class | Description |
|-------|-------------|
| `ItemStackSerializer` | Interface for item serialization |
| `BukkitItemStackSerializer` | Official Bukkit implementation using Base64 |
| `ItemStackSerializeException` | Exception thrown on serialization failure |
| `ItemStackDeserializeException` | Exception thrown on deserialization failure |

### Utilities

| Class | Description |
|-------|-------------|
| `FileUtil` | File I/O operations with charset support |
| `StrUtil` | String manipulation utilities |

### Exception Handling

| Class | Description |
|-------|-------------|
| `PluginBizException` | Business logic exception wrapper |
| `PluginConstants` | Common constants (e.g., AUTHOR) |

### Permission Module

| Class | Description |
|-------|-------------|
| `@RequiredPermission` | Annotation for permission-protected methods |
| `PermissionAspect` | AspectJ implementation for automatic permission checking |

## ⚙️ Configuration

Example `config.yml`:

```yaml
# Plugin configuration
plugin:
  name: MyPlugin
  version: 1.0.0

# Logging settings
logging:
  file:
    enable: true      # Enable file logging
    filename: mc-plugin.log

# Debug mode
debug: false
```

Access configuration in your plugin:

```java
boolean fileLoggingEnabled = getConfig().getBoolean("logging.file.enable");
```

## 📁 Project Structure

```
floyd-core/
├── src/main/java/com/floyd/core/
│   ├── FloydPlugin.java              # Main plugin base class
│   ├── PluginBizException.java       # Business exception wrapper
│   ├── PluginConstants.java          # Common constants
│   ├── inventory/io/                 # Item serialization module
│   │   ├── ItemStackSerializer.java
│   │   ├── BukkitItemStackSerializer.java
│   │   ├── ItemStackSerializeException.java
│   │   └── ItemStackDeserializeException.java
│   ├── logging/                      # Logging module
│   │   ├── ConsoleLogger.java
│   │   ├── DefaultConsoleLogger.java
│   │   └── LogConfig.java
│   ├── permission/                   # Permission module
│   │   ├── RequiredPermission.java
│   │   └── PermissionAspect.java
│   └── util/                         # Utility classes
│       ├── FileUtil.java
│       └── StrUtil.java
├── src/test/java/                    # Unit tests
├── pom.xml                           # Maven build configuration
└── LICENSE                           # Apache 2.0 License
```

## 🔨 Building from Source

### Prerequisites

- JDK 21 installed
- Maven 3.6+ installed

### Build Commands

```bash
# Clone the repository
git clone https://github.com/codeNoob2281/Floyd-Core.git
cd Floyd-Core

# Clean and compile
mvn clean package

# Skip tests (faster build)
mvn clean package -DskipTests

# Install to local Maven repository
mvn clean install
```

### Output Files

After building, you'll find these files in the `target/` directory:

- `floyd-core-{version}.jar` - Standard JAR without dependencies
- `floyd-core-{version}-shaded.jar` - Fat JAR with all dependencies included (recommended for deployment)

## 🧪 Testing

Run unit tests:

```bash
mvn test
```

View test coverage:

```bash
mvn clean test jacoco:report
```

Test reports will be generated in `target/site/jacoco/index.html`.

## 👨‍💻 Author

- **floyd** ([codeNoob2281](https://github.com/codeNoob2281))

## 🙏 Acknowledgments

- [Spring Framework](https://spring.io/projects/spring-framework) - Application framework
- [PaperMC](https://papermc.io/) - High-performance Minecraft server
- [Lombok](https://projectlombok.org/) - Boilerplate code reduction
- [AspectJ](https://www.eclipse.org/aspectj/) - AOP implementation

## 📄 License

This project is licensed under the GPL 3.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2026 floyd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow existing code style (checkstyle configured in pom.xml)
- Write unit tests for new features
- Update documentation as needed
- Use meaningful commit messages

### Reporting Issues

- Use GitHub Issues for bug reports and feature requests
- Include detailed reproduction steps
- Provide environment information (Java version, server version, etc.)

---

## ⚠️ Disclaimer

**This project is currently under active development.** The API may change between versions without prior notice. It's recommended to pin to a specific version in production environments.

### Known Limitations

- Requires Java 21 (due to Spring Framework 6.x requirements)
- Only compatible with PaperMC 1.21.11+
- GitHub Packages requires authentication for downloads

### Roadmap

- [ ] Add more utility classes
- [ ] Improve test coverage to 80%+
- [ ] Add comprehensive documentation
- [ ] Support for additional database providers
- [ ] Event system enhancements

---

<div align="center">

**If you find this project helpful, please consider giving it a ⭐ star!**

Made with ❤️ by floyd

</div>
