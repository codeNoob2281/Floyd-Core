# Java Code Style Rules

## 1. Comments Language

All comments and Javadoc must be written in **English**.

```java
// Good
/**
 * Resolves method parameters and returns a list of parameter binding metadata.
 */
public static List<ParameterBinding> resolve(Method method) { ... }

// Bad
/**
 * 解析方法参数，返回参数绑定元数据列表。
 */
public static List<ParameterBinding> resolve(Method method) { ... }
```

## 2. Brace Policy

All `if`, `else`, `for`, `while`, and `do-while` statements must use braces `{}`, even for single-line bodies.

```java
// Good
if (condition) {
    return value;
}

// Bad
if (condition) return value;
```

## 3. Naming Conventions

- **Classes/Interfaces**: PascalCase (e.g., `ParameterResolver`, `SubCommandParam`)
- **Methods**: camelCase (e.g., `resolveParameter`, `getType`)
- **Variables**: camelCase (e.g., `parameterBindings`, `defaultValue`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`)
- **Packages**: lowercase (e.g., `com.floyd.core.command`)

## 4. Code Organization

- Import statements should be grouped in order: Java, Third-party, Project
- Each group separated by a blank line
- No wildcard imports (e.g., `import java.util.*`)

## 5. Exception Handling

- Use specific exception types instead of generic `Exception`
- Exception messages should be descriptive and in English
- Do not catch and ignore exceptions silently
