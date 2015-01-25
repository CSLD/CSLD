package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Language;

import java.util.List;
import java.util.Locale;

/**
 * Created by jbalhar on 1/25/2015.
 */
public class DbLocaleProvider implements LocaleProvider {
    LocaleProvider translator = new CodeLocaleProvider();

    public DbLocaleProvider(){

    }

    @Override
    public List<Locale> availableLocale() {
        return null;
    }

    @Override
    public List<Language> availableLanguages() {
        return null;
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
