# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Install to local Maven repo
mvn clean install

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=TrieCommandCompleterTest

# Run a single test method
mvn test -Dtest=TrieCommandCompleterTest#testMethodName
```

## Project Overview

Floyd-Core is a **Minecraft plugin development framework** (library, not standalone plugin).
Built on **PaperMC 1.20.6+** and **Spring Framework 6.2.7**. Java 21, Maven 3.6+.

## Architecture

- **FloydPlugin** (abstract base) — Entry point extending `JavaPlugin`
  - `onEnable()`: init `ConsoleLogger` → create Spring context → call `initialize()`
  - `onDisable()`: call `cleanup()` → close Spring context
- **SpringConfig** — Core `@Configuration` enabling AOP and `@ComponentScan("com.floyd.core")`.
- **CommandDispatcher** — Auto-discovers `@SubCommandHandler` beans, registers Bukkit commands
  - Implements `BeanPostProcessor` + `SmartInitializingSingleton`
  - Uses `TrieCommandCompleter` for tab completion with permission filtering
- **DatabaseManager** — HikariCP connection pool (PostgreSQL, SQLite, MySQL)
  - Lightweight SQL builder in `syntax/` package (Select/Insert/Update/Delete)
- **PermissionAspect** — AspectJ `@Aspect` intercepting `@RequiredPermission` annotated methods, auto-detecting `Player` parameters.
- **I18nSettingManagerImpl** — Per-locale settings manager with fallback chains
  - Scans `classpath*:language/*` and custom language directories
- **PluginSettingsManager** — ConfigMe `SettingsManagerImpl` wrapper with reload listeners.

### Key Annotations
- `@SubCommandHandler` — Marks a Spring bean as a command handler
- `@SubCommandMapping` — Maps a method to a sub-command path
- `@RequiredPermission` — AOP-enforced permission check on method entry
- `@SettingsReloadAware` — Interface for settings reload callbacks

### Test Framework

**Dependencies**: JUnit 5.11.0 + Mockito 5.21.0 (no `spring-test` module).

**Two-tier base class hierarchy**:
- `BaseTest` — Lightweight. Initializes `ConsoleLogger` via `@BeforeAll`. Use for plain unit tests that need logging.
- `AbstractSpringTest` (extends `BaseTest`) — Spring context per test class
  - Creates `AnnotationConfigApplicationContext` from `SpringTestConfig`
  - Mocks `FloydPlugin.getPluginDataPath()` via `mockStatic`
  - Use for tests that need a Spring context

**Choosing a base class**:
- No logging or Spring needed → don't extend either (e.g., `TypeConverterTest`)
- Logging only → extend `BaseTest` (e.g., `TrieCommandCompleterTest`, `StrUtilTest`)
- Spring context needed → extend `AbstractSpringTest` (e.g., `I18nMessageProviderTest`, `PermissionAspectTest`)

**Mocking patterns**:
- `@ExtendWith(MockitoExtension.class)` + `@Mock` fields — for instance mocking
- `mockStatic()` — for static methods like `Bukkit.getPlayer()` or `FloydPlugin.getPluginDataPath()`

**Conventions**:
- Class names: `*Test` suffix
- Method names: `testXxx_Yyy` (underscore separates scenario variant)
- Bukkit types (`CommandSender`, `Player`, `Bukkit`) must be mocked — no running server in tests
- Test fixtures: inner `public static` classes or dedicated `@Component` classes in the same package
- Test resources: `src/test/resources/config.yml` for app config, `src/test/resources/language/` for i18n files

## Code Style Rules

All code must follow these rules. See [java-code-style.md](.claude/rules/java-code-style.md) for full details.

- **Comments**: All comments and Javadoc must be written in English
- **Braces**: All `if`, `else`, `for`, `while`, and `do-while` statements must use braces `{}`

## Commit Rules

Commits must use **English** and follow conventional commits format. See [commit-message.md](.claude/rules/commit-message.md) for full details.

## Development Workflow

All feature development and bug fixes must follow **TDD** (Test-Driven Development). See [test-driven-development.md](.claude/rules/test-driven-development.md) for full details.

- **Red**: Write a failing test first
- **Green**: Write minimum code to pass the test
- **Refactor**: Clean up while keeping tests green
