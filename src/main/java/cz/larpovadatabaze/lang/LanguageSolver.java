package cz.larpovadatabaze.lang;

import java.util.List;
import java.util.Locale;

/**
 * Created by jbalhar on 12/21/2014.
 */
public interface LanguageSolver {
    List<Locale> getLanguagesForUser();
    List<String> getTextLangForUser();
}
