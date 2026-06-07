package com.floyd.core.convert.provider;

import com.floyd.core.common.convert.TypeConversionException;
import com.floyd.core.common.convert.TypeConvertProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author floyd
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class PlayerTypeConvertProvider implements TypeConvertProvider {

    @Override
    public boolean support(Class<?> targetType) {
        return targetType == Player.class;
    }

    @Override
    public Object convert(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        Player player = Bukkit.getPlayer(value);
        if (player == null) {
            throw new TypeConversionException("Player " + value + " not found");
        }
        return player;
    }
}
