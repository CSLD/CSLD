package cz.larpovadatabaze.games.converters;

import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts Label to String and from String to Label.
 * Label has unique name.
 */
public class LabelConverter implements IConverter<Label> {
    private TokenSearch tokenSearch;

    public LabelConverter(TokenSearch tokenSearch) {
        this.tokenSearch = tokenSearch;
    }

    @Override
    public Label convertToObject(String labelName, Locale locale) {
        List<Label> foundLabels = tokenSearch.findLabels(labelName);
        int amountOfLabels = foundLabels.size();
        if (amountOfLabels == 1) {
            return foundLabels.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String convertToString(Label label, Locale locale) {
        String stringRepresentation = label.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
