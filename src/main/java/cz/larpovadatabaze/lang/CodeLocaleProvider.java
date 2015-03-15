package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Language;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Local implementation of provider for locale, containing hardcoded values.
 */
public class CodeLocaleProvider implements LocaleProvider, IConverter<Language> {
    private List<Locale> availableLocales;

    public CodeLocaleProvider(){
       this(Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("cs"), Locale.GERMAN, Locale.ITALY));
    }

    public CodeLocaleProvider(List<Locale> availableLocales) {
        this.availableLocales = availableLocales;
    }

    public List<Locale> availableLocale(){
        return availableLocales;
    }

    @Override
    public List<Language> availableLanguages() {
        List<Language> languages = new ArrayList<Language>();
        for(Locale locale: availableLocales) {
            languages.add(new Language(locale));
        }
        return languages;
    }

    @Override
    public String transformLocaleToName(Locale locale) {
        if(availableLocales.contains(locale)){
            return locale.getLanguage();
        } else {
            throw new UnsupportedLocaleException();
        }
    }

    @Override
    public Locale transformToLocale(String locale) {
        Locale actualLocale = Locale.forLanguageTag(locale);
        if(availableLocales.contains(actualLocale)){
            return actualLocale;
        } else {
            throw new UnsupportedLocaleException();
        }
    }

    @Override
    public Language convertToObject(String value, Locale locale) throws ConversionException {
        return new Language(transformToLocale(value));
    }

    @Override
    public String convertToString(Language value, Locale locale) {
        return transformLocaleToName(value.getLanguage());
    }
}
