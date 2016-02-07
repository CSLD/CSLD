package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.UserHasLanguages;
import cz.larpovadatabaze.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * It gets languages from session as well as from information about logged user in the database.
 */
@Service
public class DbSessionLanguageSolver implements LanguageSolver {
    public DbSessionLanguageSolver(){}

    @Override
    public List<Locale> getLanguagesForUser() {
        List<Locale> allLocales = new ArrayList<>();
        CsldUser loggedUser = UserUtils.getLoggedUser();
        if(loggedUser != null) {
            allLocales.addAll(loggedUser.getUserHasLanguages().stream()
                    .map(lang -> Locale.forLanguageTag(lang.getLanguage()))
                    .collect(Collectors.toList()));
        }
        return allLocales;
    }

    @Override
    public List<String> getTextLangForUser() {
        List<String> allLocales = new ArrayList<>();
        CsldUser loggedUser = UserUtils.getLoggedUser();
        if(loggedUser != null) {
            allLocales.addAll(loggedUser.getUserHasLanguages()
                    .stream()
                    .map(UserHasLanguages::getLanguage)
                    .collect(Collectors.toList()));
        }
        return allLocales;
    }
}
