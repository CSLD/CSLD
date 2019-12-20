package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.Labels;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts Label to String and from String to Label.
 * Label has unique name.
 */
public class LabelConverter implements IConverter<Label> {
    private Labels labels;

    public LabelConverter(Labels labels) {
        this.labels = labels;
    }

    @Override
    public Label convertToObject(String labelName, Locale locale) {
        try {
            List<Label> foundLabels = labels.getByAutoCompletable(labelName);
            int amountOfLabels = foundLabels.size();
            if (amountOfLabels == 1) {
                return foundLabels.get(0);
            } else {
                return null;
            }
        } catch(WrongParameterException ex) {
            return null;
        }
    }

    @Override
    public String convertToString(Label label, Locale locale) {
        String stringRepresentation = label.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
