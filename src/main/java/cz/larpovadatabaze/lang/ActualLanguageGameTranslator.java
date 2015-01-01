package cz.larpovadatabaze.lang;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.GameHasLanguages;
import cz.larpovadatabaze.entities.Language;
import org.apache.wicket.Session;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ActualLanguageGameTranslator implements Translator<Game> {
    private LanguageSolver whatLanguagesToUse;
    private LocaleProvider localeProvider;

    public ActualLanguageGameTranslator(LanguageSolver whatLanguagesToUse) {
        this.whatLanguagesToUse = whatLanguagesToUse;
        localeProvider = new CodeLocaleProvider();
    }

    @Override
    public Collection<Game> translateAll(Collection<Game> toTranslate) {
        for(Game game: toTranslate) {
            translate(game);
        }
        return toTranslate;
    }

    @Override
    public Game translate(Game toTranslate) {
        if(toTranslate.getAvailableLanguages() == null) {
            return toTranslate;
        }

        if(translateIntoCurrent(toTranslate)){
            return toTranslate;
        }

        if(translateIntoAvailableForUser(toTranslate)){
            return toTranslate;
        }

        translateIntoDefault(toTranslate);
        return toTranslate;
    }

    private void translateIntoDefault(Game toTranslate) {
        List<GameHasLanguages> translationsForGame =  toTranslate.getAvailableLanguages();

        toTranslate.setDescription(translationsForGame.get(0).getDescription());
        toTranslate.setName(translationsForGame.get(0).getName());
        toTranslate.setLang(localeProvider.transformLocaleToName(translationsForGame.get(0).getLanguageForGame().getLanguage()));
    }

    private boolean translateIntoAvailableForUser(Game toTranslate) {
        List<Locale> otherAvailableLocales = whatLanguagesToUse.getLanguagesForUser();
        for(GameHasLanguages language: toTranslate.getAvailableLanguages()) {
            for(Locale availableLocale: otherAvailableLocales) {
                if(language.getLanguageForGame().equals(new Language(availableLocale))) {
                    toTranslate.setDescription(language.getDescription());
                    toTranslate.setName(language.getName());
                    toTranslate.setLang(localeProvider.transformLocaleToName(language.getLanguageForGame().getLanguage()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean translateIntoCurrent(Game toTranslate){
        Language actualLanguage = new Language(Session.get().getLocale());
        List<GameHasLanguages> translationsForGame = toTranslate.getAvailableLanguages();
        for(GameHasLanguages language: translationsForGame) {
            if(language.getLanguageForGame().equals(actualLanguage)) {
                toTranslate.setDescription(language.getDescription());
                toTranslate.setName(language.getName());
                toTranslate.setLang(localeProvider.transformLocaleToName(language.getLanguageForGame().getLanguage()));
                return true;
            }
        }
        return false;
    }
}