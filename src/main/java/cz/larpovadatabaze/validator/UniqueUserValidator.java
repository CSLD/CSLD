package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 *
 */
public class UniqueUserValidator implements IValidator<String> {
    CsldUserService personService;

    private boolean updateExisting;

    public UniqueUserValidator(boolean updateExisting, CsldUserService personService){
        this.personService = personService;
        this.updateExisting = updateExisting;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        CsldUser existing = personService.getByEmail(validatable.getValue());

        if(!updateExisting && (existing != null)) {
            error(validatable, "person-exists");
        }
        if(updateExisting && (existing == null)) {
            error(validatable, "update-nonexistent");
        }
    }

    private void error(IValidatable<String> validatable, String message) {
        ValidationError error = new ValidationError();
        error.setMessage(message);
        validatable.error(error);
    }
}
