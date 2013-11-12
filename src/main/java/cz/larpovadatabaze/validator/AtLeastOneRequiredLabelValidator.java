package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.components.panel.game.ChooseLabelsPanel;
import cz.larpovadatabaze.entities.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.List;

/**
 *
 */
public class AtLeastOneRequiredLabelValidator implements IValidator{
    @Override
    public void validate(IValidatable validatable) {
        List<Label> labels = (List<Label>) validatable.getValue();
        boolean hasRequired = false;
        for(Label label: labels) {
            if(label.getRequired()){
                hasRequired = true;
                break;
            }
        }

        if(!hasRequired) {
            error(validatable, "one-required-label");
        }
    }

    private void error(IValidatable<String> validatable, String message) {
        ValidationError error = new ValidationError();
        error.addKey(message);
        validatable.error(error);
    }
}
