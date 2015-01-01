package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Language;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class CodeLocaleProvideTest {
    private CodeLocaleProvider provider = new CodeLocaleProvider();

    @Test
    public void getLocaleNameReturnsCorrectLocaleForSupportedLanguage() {
        assertThat(provider.transformLocaleToName(Locale.GERMAN), is(Locale.GERMAN.getLanguage()));
    }

    @Test(expected = UnsupportedLocaleException.class)
    public void getLocaleThrowsExceptionWhenUnsupportedLanguage() {
        provider.transformLocaleToName(Locale.JAPAN);
    }

    @Test
    public void transformToLocaleTransformsToCorrectSupportedLanguage() {
        assertThat(provider.transformToLocale("de"), is(Locale.GERMAN));
    }

    @Test(expected = UnsupportedLocaleException.class)
    public void transformToLocaleThrowsExceptionForUnsupportedLanguage() {
        provider.transformToLocale("jp");
    }

    @Test
    public void availableLanguagesReturnsLanguagesAdequateForGivenLocale() {
        List<Locale> availableLocale = Arrays.asList(Locale.GERMAN, Locale.ENGLISH);
        List<Language> availableLanguages = Arrays.asList(new Language(Locale.GERMAN), new Language(Locale.ENGLISH));

        CodeLocaleProvider localeProvider = new CodeLocaleProvider(availableLocale);

        assertThat(localeProvider.availableLanguages(), contains(availableLanguages.toArray()));
    }

    @Test
    public void convertExistingToObject() {
        List<Locale> availableLocale = Arrays.asList(Locale.GERMAN, Locale.ENGLISH);
        CodeLocaleProvider localeProvider = new CodeLocaleProvider(availableLocale);
        assertThat(localeProvider.convertToObject(Locale.GERMAN.getLanguage(), Locale.ENGLISH), is(new Language(Locale.GERMAN)));
    }

    @Test
    public void convertExistingToString() {
        List<Locale> availableLocale = Arrays.asList(Locale.GERMAN, Locale.ENGLISH);
        CodeLocaleProvider localeProvider = new CodeLocaleProvider(availableLocale);
        assertThat(localeProvider.convertToString(new Language(Locale.GERMAN), Locale.ENGLISH), is(Locale.GERMAN.getLanguage()));
    }
}
