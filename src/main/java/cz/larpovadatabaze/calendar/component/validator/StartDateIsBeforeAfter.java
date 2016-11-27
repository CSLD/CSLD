package cz.larpovadatabaze.calendar.component.validator;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.StringResourceModel;

import java.util.Date;

/**
 * It verifies on two TimeBasedComponent that one is before the other.
 */
public class StartDateIsBeforeAfter extends AbstractFormValidator {
    private FormComponent startDate;
    private FormComponent endDate;

    public StartDateIsBeforeAfter(FormComponent startDate, FormComponent endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return new FormComponent<?>[]{
                startDate, endDate
        };
    }

    @Override
    public void validate(Form<?> form) {
        Date start = ((Date) startDate.getConvertedInput());
        Date end = ((Date) endDate.getConvertedInput());
        if(!end.after(start)) {
            endDate.error(new StringResourceModel("event.endIsBeforeStart", startDate, null).getString());
        }
    }
}
