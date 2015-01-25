package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.dao.LanguageDao;
import cz.larpovadatabaze.entities.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jbalhar on 1/25/2015.
 */
public class DbLocaleProvider implements LocaleProvider {
    private LocaleProvider translator = new CodeLocaleProvider();
    private LanguageDao languages;

    public DbLocaleProvider(LanguageDao languages){
        this.languages = languages;
    }

    @Override
    public List<Locale> availableLocale() {
        List<Language> available = languages.findAll();
        List<Locale> locales = new ArrayList<Locale>();
        for(Language lang: available) {
            locales.add(lang.getLanguage());
        }
        return locales;
    }

    @Override
    public List<Language> availableLanguages() {
        return languages.findAll();
    }

    @Override
    public String transformLocaleToName(Locale locale) {
        return translator.transformLocaleToName(locale);
    }

    @Override
    public Locale transformToLocale(String locale) {
        return translator.transformToLocale(locale);
    }
}
