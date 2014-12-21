package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.dao.LanguageDao;
import cz.larpovadatabaze.entities.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of LocaleProvide, which can return Language as well as Locale.
 */
public class JpaLocaleProvider implements LocaleProvider {
    private LanguageDao languages;

    public JpaLocaleProvider(LanguageDao languages) {
        this.languages = languages;
    }

    @Override
    public List<Locale> availableLocale() {
        List<Language> availableLanguages = availableLanguages();
        List<Locale> availableLocales = new ArrayList<Locale>();
        for(Language lang: availableLanguages) {
            availableLocales.add(lang.getLanguage());
        }
        return availableLocales;
    }

    @Override
    public List<Language> availableLanguages() {
        return languages.findAll();
    }

    @Override
    public String transformLocaleToName(Locale locale) {
        if(availableLocale().contains(locale)){
            return locale.getDisplayName();
        } else {
            throw new UnsupportedLocaleException();
        }
    }

    @Override
    public Locale transformToLocale(String locale) {
        Locale actualLocale = Locale.forLanguageTag(locale);
        if(availableLocale().contains(actualLocale)){
            return actualLocale;
        } else {
            throw new UnsupportedLocaleException();
        }
    }


}
