package util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationManager {

    private static ResourceBundle bundle;
    private static Locale currentLocale = Locale.ENGLISH;

    static {
        loadBundle();
    }

    private static void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle("messages", currentLocale);
        } catch (MissingResourceException e) {
            // Week 7: Exception handling — graceful fallback if no bundle found
            bundle = null;
        }
    }

    public static String get(String key) {
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return key;  // Fallback: return the key itself
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        loadBundle();
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
