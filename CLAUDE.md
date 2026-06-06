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

Floyd-Core is a **Minecraft plugin development framework** (library, not standalone plugin) built on **PaperMC 1.20.6+** and **Spring Framework 6.2.7**. Java 21, Maven 3.6+.

## Architecture

- **FloydPlugin** (abstract base) — Entry point extending `JavaPlugin`. On `onEnable()`, initializes `ConsoleLogger`, creates Spring `AnnotationConfigApplicationContext` with `SpringConfig` + user config classes, then calls abstract `initialize()`. On `onDisable()`, calls `cleanup()` then closes Spring context.
- **SpringConfig** — Core `@Configuration` enabling AOP and `@ComponentScan("com.floyd.core")`.
- **CommandDispatcher** — `BeanPostProcessor` + `SmartInitializingSingleton` that auto-discovers `@SubCommandHandler` beans and registers Bukkit command executors. Uses `TrieCommandCompleter` for tab completion with permission filtering.
- **DatabaseManager** — HikariCP-based connection pool supporting PostgreSQL, SQLite, MySQL. Contains a lightweight ORM-like SQL builder (`syntax/` package with fluent Select/Insert/Update/Delete).
- **PermissionAspect** — AspectJ `@Aspect` intercepting `@RequiredPermission` annotated methods, auto-detecting `Player` parameters.
- **I18nSettingManagerImpl** — ConfigMe-based per-locale settings manager that scans `classpath*:language/*` and custom language directories, with locale fallback chains.
- **PluginSettingsManager** — ConfigMe `SettingsManagerImpl` wrapper with reload listeners.

### Key Annotations
- `@SubCommandHandler` — Marks a Spring bean as a command handler
- `@SubCommandMapping` — Maps a method to a sub-command path
- `@RequiredPermission` — AOP-enforced permission check on method entry
- `@SettingsReloadAware` — Interface for settings reload callbacks

### Test Patterns

Tests use JUnit 5 + Mockito. Abstract base classes:
- `BaseTest` — Initializes `ConsoleLogger` via `@BeforeAll`
- `AbstractSpringTest` — Extends `BaseTest`, creates Spring context from `SpringTestConfig` (`@ComponentScan("com.floyd.core")`), mocks `FloydPlugin.getPluginDataPath()`

## Code Style Rules

All code must follow these rules. See [java-code-style.md](.claude/rules/java-code-style.md) for full details.

- **Comments**: All comments and Javadoc must be written in English
- **Braces**: All `if`, `else`, `for`, `while`, and `do-while` statements must use braces `{}`

## Commit Rules

Commits must use **English** and follow conventional commits format. See [commit-message.md](.claude/rules/commit-message.md) for full details.
