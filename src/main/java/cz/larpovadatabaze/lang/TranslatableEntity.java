package cz.larpovadatabaze.lang;

import java.util.List;

/**
 * Basic translatable interface.
 */
public interface TranslatableEntity extends Translation {
    void setLang(String lang);
    List<TranslationEntity> getLanguages();
}
