package cz.larpovadatabaze.lang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Translator for all entities containing name and description.
 */
@Service
public class TranslatableEntityTranslator implements Translator<TranslatableEntity> {
    private LanguageSolver whatLanguagesToUse;
    private LanguageSolver actualLanguageProvider = new SessionLanguageSolver();

    @Autowired
    public TranslatableEntityTranslator(LanguageSolver whatLanguagesToUse) {
        this.whatLanguagesToUse = whatLanguagesToUse;
    }

    public TranslatableEntityTranslator(LanguageSolver whatLanguagesToUse, LanguageSolver actualLanguageProvider) {
        this(whatLanguagesToUse);
        this.actualLanguageProvider = actualLanguageProvider;
    }

    @Override
    public Collection<TranslatableEntity> translateAll(Collection<TranslatableEntity> toTranslate) {
        for(TranslatableEntity game: toTranslate) {
            translate(game);
        }
        return toTranslate;
    }

    @Override
    public TranslatableEntity translate(TranslatableEntity toTranslate) {
        if(toTranslate.getLanguages() == null || toTranslate.getLanguages().isEmpty()) {
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

    private void translateUsingEntity(TranslatableEntity toTranslate, TranslationEntity translation) {
        toTranslate.setDescription(translation.getDescription());
        toTranslate.setName(translation.getName());
        toTranslate.setLang(translation.getLanguage());
    }

    private void translateIntoDefault(TranslatableEntity toTranslate) {
        List<TranslationEntity> translationsForGame =  toTranslate.getLanguages();
        translateUsingEntity(toTranslate, translationsForGame.get(0));
    }

    private boolean translateIntoAvailableForUser(TranslatableEntity toTranslate) {
        List<Locale> otherAvailableLocales = whatLanguagesToUse.getLanguagesForUser();
        List<TranslationEntity> translations = toTranslate.getLanguages();

        for(TranslationEntity language: translations) {
            for(Locale availableLocale: otherAvailableLocales) {
                if(language.getLanguage().equals(availableLocale.getLanguage())) {
                    translateUsingEntity(toTranslate, language);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean translateIntoCurrent(TranslatableEntity toTranslate){
        String actualLanguage = actualLanguageProvider.getLanguagesForUser().get(0).getLanguage();
        List<TranslationEntity> translationsForGame = toTranslate.getLanguages();
        for(TranslationEntity language: translationsForGame) {
            if(language.getLanguage() != null &&
                    language.getLanguage().equals(actualLanguage)) {
                translateUsingEntity(toTranslate, language);
                return true;
            }
        }
        return false;
    }
}
