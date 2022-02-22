package de.notion.common.localization.context.def;

import de.notion.common.localization.MessageProvider;
import de.notion.common.localization.context.Contextualizer;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocaleMessageProvider implements MessageProvider<Locale> {

    private final Map<Locale, ResourceBundle> bundles;
    private final Contextualizer<Locale> contextualizer;

    private LocaleMessageProvider(Map<Locale, ResourceBundle> bundles, Contextualizer<Locale> contextualizer) {
        this.bundles = bundles;
        this.contextualizer = contextualizer;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String string(Locale locale, String key, Object... params) {
        var context = contextualizer.locale(locale);
        var bundle = bundle(context);
        var bundleString = bundle.getString(key);
        var messageFormat = new MessageFormat(bundleString, context);

        return messageFormat.format(params);
    }

    @Override
    public String[] stringArray(Locale locale, String key, Object... params) {
        var context = contextualizer.locale(locale);
        var bundle = bundle(context);
        var bundleArray = bundle.getStringArray(key);
        var localizedArray = new String[bundleArray.length];

        for (int i = 0; i < bundleArray.length; i++) {
            var line = bundleArray[i];
            var messageFormat = new MessageFormat(line, context);
            localizedArray[i] = messageFormat.format(params);
        }

        return localizedArray;
    }

    @Override
    public List<String> stringList(Locale locale, String key, Object... params) {
        return Arrays.asList(stringArray(locale, key, params));
    }

    private ResourceBundle bundle(Locale locale) {
        var bundle = bundles.get(locale);

        if (bundle == null) {
            throw new MissingResourceException(
                    "Can't find resource bundle, locale " + locale.getLanguage(),
                    ResourceBundle.class.getName(),
                    locale.getLanguage()
            );
        }
        return bundle;
    }

    public static class Builder extends AbstractBuilder<Locale> {

        @Override
        public MessageProvider<Locale> build() {
            return new LocaleMessageProvider(this.locales, this.contextualizer);
        }
    }
}
