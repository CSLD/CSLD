package cz.larpovadatabaze.validator;

import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import java.util.List;

/**
 * User: Michal Kara Date: 4.7.15 Time: 10:23
 */
public class NonEmptyAuthorsValidator implements INullAcceptingValidator<List<?>> {
    @Override
    public void validate(IValidatable<List<?>> validatable) {
        if ((validatable.getValue() == null) || (validatable.getValue().isEmpty())) {
            ValidationError error = new ValidationError();
            error.addKey("not-empty-authors");
            validatable.error(error);
        }
    }
}
