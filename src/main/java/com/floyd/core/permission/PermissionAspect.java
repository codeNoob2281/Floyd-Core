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
import org.bukkit.entity.Player;

/**
 * @author floyd
 * @date 2026/3/28
 */
@Aspect
public class PermissionAspect {

    @Pointcut("@annotation(com.floyd.core.permission.RequiredPermission)")
    public void requiredPermission() {
    }

    @Around("requiredPermission()")
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
            RequiredPermission permAnnotation = methodSignature.getMethod().getAnnotation(RequiredPermission.class);
            String permValue = permAnnotation.value();
            if (StrUtil.isEmpty(permValue)) {
                throw new PluginBizException("empty permission value is not allowed");
            }
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
        }
        return jp.proceed();
    }

}
