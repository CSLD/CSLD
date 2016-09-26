package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.hibernate.NonUniqueResultException;

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
        // TODO - I am not sure, if this is the right place.
        try {
            CsldUser existing = personService.getByEmail(validatable.getValue());
            CsldUser loggedIn = UserUtils.getLoggedUser();

            if(!updateExisting && (existing != null)) {
                error(validatable, "person-exists");
            }

            if(updateExisting && !existing.getId().equals(loggedIn.getId())) {
                error(validatable, "update-nonexistent");
            }
        } catch (NonUniqueResultException ex) {
            // If there are already two existing such persons.
            error(validatable, "person-exists");
        }
    }

    private void error(IValidatable<String> validatable, String message) {
        ValidationError error = new ValidationError();
        error.setMessage(message);
        validatable.error(error);
    }
}
