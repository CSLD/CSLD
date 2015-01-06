package cz.larpovadatabaze.lang;

import java.util.List;

/**
 * Basic translatable interface.
 */
public interface TranslatableEntity extends Translation {
    public void setLang(String lang);
    public List<TranslationEntity> getLanguages();
}
