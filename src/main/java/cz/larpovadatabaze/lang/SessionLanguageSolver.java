package cz.larpovadatabaze.lang;

import org.apache.wicket.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SessionLanguageSolver implements LanguageSolver, Serializable {
    @Override
    public List<Locale> getLanguagesForUser() {
        List<Locale> localeForUser = new ArrayList<Locale>();
        localeForUser.add(Session.get().getLocale());
        return localeForUser;
    }

    @Override
    public List<String> getTextLangForUser() {
        List<String> langForUser =  new ArrayList<String>();
        langForUser.add(Session.get().getLocale().getLanguage());
        return langForUser;
    }
}
