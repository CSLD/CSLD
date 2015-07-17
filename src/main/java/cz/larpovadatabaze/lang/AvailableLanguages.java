package cz.larpovadatabaze.lang;

import java.util.*;

/**
 * Languages available to use with the larp database.
 */
public enum AvailableLanguages {
    cs,
    en,
    de,
    it;

    public static List<Locale> availableLocale() {
        List<Locale> available = new ArrayList<>();
        Collections.addAll(available, Locale.forLanguageTag(cs.name()), Locale.forLanguageTag(en.name()),
                Locale.forLanguageTag(de.name()), Locale.forLanguageTag(it.name()));
        return available;
    }

    public static List<String> availableLocaleNames() {
        List<String> available = new ArrayList<>();
        Collections.addAll(available, cs.name(), en.name(), de.name(), it.name());
        return available;
    }
}
