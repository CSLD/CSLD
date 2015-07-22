package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.UserHasLanguages;
import cz.larpovadatabaze.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * It gets languages from session as well as from information about logged user in the database.
 */
@Service
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
            for(UserHasLanguages lang: loggedUser.getUserHasLanguages()){
                allLocales.add(Locale.forLanguageTag(lang.getLanguage()));
            }
        }
        return allLocales;
    }

    @Override
    public List<String> getTextLangForUser() {
        List<String> allLocales = solveLang.getTextLangForUser();
        CsldUser loggedUser = UserUtils.getLoggedUser();
        if(loggedUser != null) {
            for(UserHasLanguages lang: loggedUser.getUserHasLanguages()){
                allLocales.add(lang.getLanguage());
            }
        }
        return allLocales;
    }
}
