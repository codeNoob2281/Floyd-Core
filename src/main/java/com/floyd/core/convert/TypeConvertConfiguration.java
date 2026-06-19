package com.floyd.core.convert;

import com.floyd.core.common.convert.TypeConvertProvider;
import com.floyd.core.common.convert.TypeConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author floyd
 */
@Configuration
public class TypeConvertConfiguration implements InitializingBean {

    @Autowired
    List<TypeConvertProvider> typeConvertProviders;

    @Override
    public void afterPropertiesSet() throws Exception {
        typeConvertProviders.forEach(TypeConverter::addProvider);
    }
}
