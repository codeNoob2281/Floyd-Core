package com.floyd.core.permission;

import com.floyd.core.PluginBizException;
import com.floyd.core.util.StrUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author floyd
 */
@Aspect
public class PermissionAspect {

    @Pointcut("@annotation(com.floyd.core.permission.RequiredPermission)")
    public void requiredPermission() {
    }

    @Around("requiredPermission()")
    public Object aroundNeedPermission(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        RequiredPermission permAnnotation = methodSignature.getMethod().getAnnotation(RequiredPermission.class);
        String permValue = permAnnotation.value();
        if (StrUtil.isEmpty(permValue)) {
            throw new PluginBizException("empty permission value is not allowed");
        }

        // 如果是控制台发起的调用，默认有所有权限，直接执行
        boolean isConsole = Arrays.stream(jp.getArgs())
                .anyMatch(arg -> (arg instanceof ConsoleCommandSender || arg instanceof RemoteConsoleCommandSender));
        if (isConsole) {
            return jp.proceed();
        }

        // 校验玩家的权限
        Player issueCmdPlayer = Arrays.stream(jp.getArgs())
                .filter(Player.class::isInstance)
                .findFirst()
                .map(Player.class::cast)
                .orElseThrow(() -> new PluginBizException("No Player parameter found in method annotated with @RequiredPermission"));

        if (!issueCmdPlayer.hasPermission(permValue)) {
            String errMsg = StrUtil.EMPTY;
            if (permAnnotation.tipPermValue()) {
                errMsg += "the permission [" + permValue + "] is required, ";
            }
            errMsg += permAnnotation.message();
            issueCmdPlayer.sendMessage(Component.text(errMsg, NamedTextColor.RED));
            Class<?> returnType = methodSignature.getMethod().getReturnType();
            if (returnType == boolean.class || returnType == Boolean.class) {
                return false;
            }
            return null;
        }
        return jp.proceed();
    }

}
