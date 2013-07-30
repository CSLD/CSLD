package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.LabelService;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts Label to String and from String to Label.
 * Label has unique name.
 */
public class LabelConverter implements IConverter<Label> {
    private LabelService labelService;
    public LabelConverter(LabelService labelService){
        this.labelService = labelService;
    }

    @Override
    public Label convertToObject(String labelName, Locale locale) {
        try {
            List<Label> foundLabels = labelService.getByAutoCompletable(labelName);
            int amountOfLabels = foundLabels.size();
            if(amountOfLabels == 1) {
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
        return label.getAutoCompleteData();
    }
}
