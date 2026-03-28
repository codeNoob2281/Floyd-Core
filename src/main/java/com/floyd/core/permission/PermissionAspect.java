package com.floyd.core.permission;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bukkit.entity.Player;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Aspect
public class PermissionAspect {

    @Pointcut("@annotation(com.floyd.core.permission.NeedPermission)")
    public void needPermission() {
    }

    @Around("needPermission()")
    public Object aroundNeedPermission(ProceedingJoinPoint jp) throws Throwable {
        Player issueCmdPlayer = null;
        for (Object arg : jp.getArgs()) {
            if (arg instanceof Player) {
                issueCmdPlayer = (Player) arg;
                break;
            }
        }
        if (issueCmdPlayer != null) {
            MethodSignature methodSignature = (MethodSignature) jp.getSignature();
            NeedPermission permAnnotation = methodSignature.getMethod().getAnnotation(NeedPermission.class);
            String permValue = permAnnotation.value();
            if (!issueCmdPlayer.hasPermission(permValue)) {
                issueCmdPlayer.sendMessage(Component.text("你没有权限执行此命令，缺少权限节点：" + permValue, NamedTextColor.RED));
                return true;
            }
        }
        return jp.proceed();
    }

}
