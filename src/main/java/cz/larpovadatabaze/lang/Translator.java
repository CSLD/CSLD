package cz.larpovadatabaze.lang;

import java.util.Collection;

public interface Translator<T extends TranslatableEntity> {
    /**
     * It allows you to toTranslate given entity.
     *
     * @param toTranslate Entity which should be translated.
     * @return
     */
    T translate(T toTranslate);

    /**
     * It translates all elements in the collection.
     *
     * @param toTranslate All entities for translation.
     * @return
     */
    Collection<T> translateAll(Collection<T> toTranslate);
}
