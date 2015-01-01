package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Language;

import java.util.List;
import java.util.Locale;

/**
 * Interface for anyone who provides languages and associated operations to the application.
 */
public interface LocaleProvider {
    /**
     * It returns list of actually supported locales.
     *
     * @return List of locales supported by larp database right now.
     */
    List<Locale> availableLocale();

    /**
     * It returns list of actually supported languages.
     *
     * @return List of available languages
     */
    List<Language> availableLanguages();

    /**
     * Transforms locale to its string representation.
     *
     * @param locale mustn't be null
     * @return String containing the translation locale, if the locale is among supported, otherwise throws exception.
     */
    String transformLocaleToName(Locale locale);

    /**
     * It returns Locale from the String, if there is one among supported.
     *
     * @param locale mustn't be null
     * @return Locale from given locale, if locale for this key exists and is among supported.
     */
    Locale transformToLocale(String locale);
}
