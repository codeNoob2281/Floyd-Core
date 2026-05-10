package com.floyd.core.i18n;

import com.floyd.core.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author floyd
 */
public class YmlSupportedMessageSource extends ReloadableResourceBundleMessageSource {

    private static final List<String> YML_EXTENSIONS = List.of(".yml", ".yaml");

    public YmlSupportedMessageSource() {
        setFileExtensions();
    }

    private void setFileExtensions() {
        List<String> list = new ArrayList<>();
        list.add(".xml");
        list.add(".properties");
        list.addAll(YML_EXTENSIONS);
        setFileExtensions(list);
    }

    @Override
    protected @NotNull Properties loadProperties(@NotNull Resource resource, @NotNull String filename) throws IOException {
        String fileNameSuffix = "." + FileUtil.getFileNameSuffix(resource.getFile().getName());
        if (YML_EXTENSIONS.contains(fileNameSuffix)) {
            return loadYaml(resource);
        }
        return super.loadProperties(resource, filename);
    }

    /**
     * Calculate the filenames for the given bundle basename and Locale,
     * appending language code, country code, and variant code.
     * <p>For example, basename "messages", Locale "de_AT_oo" &rarr; "messages_de_AT_OO",
     * "messages_de_AT", "messages_de".
     * <p>Follows the rules defined by {@link java.util.Locale#toString()}.
     *
     * @param basename the basename of the bundle
     * @param locale   the locale
     * @return the List of filenames to check
     */
    @Override
    protected List<String> calculateFilenamesForLocale(@NotNull String basename, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);

        if (!basename.isBlank()) {
            char lch = basename.charAt(basename.length() - 1);
            if (lch != '\\' && lch != '/') {
                temp.append('_');
            }
        }

        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }

    private Properties loadYaml(Resource resource) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);
        return factoryBean.getObject();
    }
}
