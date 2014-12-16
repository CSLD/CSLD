package cz.larpovadatabaze.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by jbalhar on 11/30/2014.
 */
public class AvailableLocale {
    public List<Locale> availableLocale(){
        return Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("cs"), Locale.GERMAN);
    }
}
