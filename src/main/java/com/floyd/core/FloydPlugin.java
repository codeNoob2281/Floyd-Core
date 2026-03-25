package com.floyd.core;

import com.floyd.core.logging.ConsoleLogger;
import com.floyd.core.logging.DefaultConsoleLogger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.Properties;

/**
 * @author floyd
 * @date 2026/3/22
 */
public abstract class FloydPlugin extends JavaPlugin {

    private static FloydPlugin floydPlugin;

    private static DefaultConsoleLogger consoleLogger;

    private static final String LOG_FILE_NAME = "mc-plugin.log";

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void onEnable() {
        printBanner();
        floydPlugin = this;
        // 初始化默认配置
        initConfig();
        // 初始化logger
        initConsoleLogger();
        // 初始化spring容器
        initSpringApplication();
        // 自定义的插件初始化逻辑
        initialize();
        getLogger().info("感谢使用插件：" + getPluginName());
        getLogger().info("作者：" + PluginConstants.AUTHOR);
    }

    @Override
    public void onDisable() {
        // 自定义的插件禁用逻辑
        cleanup();
        // 关闭日志文件写入
        consoleLogger.closeFileWriter();
        getLogger().info(getPluginName() + "插件已禁用，感谢使用");
        getLogger().info("作者：" + PluginConstants.AUTHOR);
    }

    public static FloydPlugin instance() {
        return floydPlugin;
    }

    protected abstract void initialize();

    protected void initSpringApplication() {
        applicationContext = new AnnotationConfigApplicationContext(getConfigClasses());
    }


    protected Class<?>[] getConfigClasses() {
        return new Class<?>[0];
    }

    protected abstract void cleanup();

    protected void initConfig() {
        saveDefaultConfig();
    }

    protected void initConsoleLogger() {
        Properties logProperties = new Properties();
        logProperties.setProperty("file.logFileEnabled", getConfig().getString("logging.file.enable"));
        consoleLogger = new DefaultConsoleLogger(getLogger(), new File(getDataFolder(), LOG_FILE_NAME), logProperties);
    }

    public static ConsoleLogger logger() {
        return consoleLogger;
    }

    public abstract String getPluginName();

    private void printBanner() {
        String banner = getBanner();
        if (banner != null && !banner.isBlank()) {
            String[] splitLines = banner.split("\n");
            for (String splitLine : splitLines) {
                getLogger().info(splitLine);
            }
        }
    }

    protected String getBanner() {
        return "";
    }
}
