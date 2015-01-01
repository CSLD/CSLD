package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.utils.UserUtils;

import java.util.List;
import java.util.Locale;

/**
 * It gets languages from session as well as from information about logged user in the database.
 */
public class DbSessionLanguageSolver implements LanguageSolver {
    private LanguageSolver solveLang;

    public DbSessionLanguageSolver(){
        solveLang = new SessionLanguageSolver();
    }

    @Override
    public List<Locale> getLanguagesForUser() {
        List<Locale> allLocales = solveLang.getLanguagesForUser();
        CsldUser loggedUser = UserUtils.getLoggedUser();
        if(loggedUser != null) {
            for(Language lang: loggedUser.getUserHasLanguages()){
                allLocales.add(lang.getLanguage());
            }
        }
        return allLocales;
    }

    @Override
    public List<String> getTextLangForUser() {
        List<String> allLocales = solveLang.getTextLangForUser();
        CsldUser loggedUser = UserUtils.getLoggedUser();
        if(loggedUser != null) {
            for(Language lang: loggedUser.getUserHasLanguages()){
                allLocales.add(lang.getLanguage().getLanguage());
            }
        }
        return allLocales;
    }
}
