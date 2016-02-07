package cz.larpovadatabaze.lang;

import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Simple model for list of languages.
 */
public class LanguagesModel extends Model {
    public LanguagesModel(List<String> availableLanguages) {
        this.setObject((Serializable) availableLanguages);
    }
}
