# Changelog

## [1.0.6] - 2026-07-19

### Features
- Add parameter-level tab completion support (ee8af52)
- Add declarative parameter binding system for sub-commands (ee9061f)

### Refactors
- Extract command execution into SubCommandExecutor (9705d6c)
- Restructure packages and introduce type converter provider pattern (1992050)
- Optimize the code based on code review (321b483, ea1b6c8)

### Chores
- Optimize Claude Code configuration for readability (873d490)

## [1.0.5] - 2026-05-23

### Features
- Optimize the language matching logic (40fdd6e)
- Modify the default value of i18n configuration (90df139)

### Bug Fixes
- Prevent NPE and reorder initialization in reload flow (ca4e096)
- Compatible with Minecraft version 1.20.6 (2b8f51d)

### Chores
- Add CLAUDE.md (c78f63e)

## [1.0.4] - 2025-06-11

### Features
- Add the i18n module (6f0fe20)
- Support loading language files from an external source (9ed3d76)

### Bug Fixes
- Fix error while language configuration does not exist (1e104ff)

### Refactors
- Reconstruct i18n message provider (8ba1d3e)
- Remove the i18n message cache configuration (27904e5)
- Revise some code review issues (afb0ac6)
- Optimizing the Bean Definition of Configuration Classes (cbb3fcc)
- Add BaseTest (383b022)
- Reconstruct the initialization logic of the log (2eed4ca)

### Documentation
- Add API references and system documentation (4eb72f2)

### Build
- Bump version to 1.0.4 (88d1725)

## [1.0.3] - 2025-03-11

### Features
- Add the simple implementation of command dispatcher (3495f2b)
- Add ORM implementation (aa5c6db)

### Bug Fixes
- Fix some bugs in Command Dispatcher (8992f33)
- Fix test run failed (53c763e)
- Solve the problem of console command permission verification (3fbf2b6)

### Refactors
- Uniformly use the Logger interface and implement parameterized logging (a2094cd)
- Reconstruction of the log module and some issue fixes (9cc6fca)
- Optimize database management and log recording functions (d2e50b4)
- Optimize file IO when import from csv (11ce334)

### Performance
- Optimize the performance of the getAllValues() in TrieNode (e21c5a5)

### Documentation
- Update docs (cd48a32)

### Build
- Bump version to 1.0.3 (1de0099)

## [1.0.2] - 2025-01-27

### Features
- Add command completion with permission check (6dc7486)
- Add partial command completion and string optimization (72dd3b9)
- Add command completion feature (d36c9f4)
- Add date utility class and optimize file utility (c44e8a1)

### Refactors
- Refactor core module serialization, logging and permission handling (a9c8fbd)
- Refactor logging system, remove date comments (d95e99b)

### Build
- Update version to 1.0.2 (2be2036)

## [1.0.1] - 2024-12-05

### Features
- Implement AOP-based permission annotation (91feb58)
- Integrate Spring AOP for permission aspect (9639c59)

### Chores
- Change license to GPLv3 (d670a5d)

## [1.0.0] - 2024-11-21

### Features
- Initial release with core plugin framework
- Spring Framework integration
- Command dispatcher and tab completion
- Database manager with HikariCP and SQL builder
- Permission aspect with annotated permission checking
- ConfigMe-based settings management
- Logging system
