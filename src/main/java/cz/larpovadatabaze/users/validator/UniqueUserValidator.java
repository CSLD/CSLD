package cz.larpovadatabaze.users.validator;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.hibernate.NonUniqueResultException;

/**
 *
 */
public class UniqueUserValidator implements IValidator<String> {
    CsldUsers personService;
    AppUsers appUsers;

    private boolean updateExisting;

    public UniqueUserValidator(boolean updateExisting, CsldUsers personService, AppUsers appUsers) {
        this.personService = personService;
        this.updateExisting = updateExisting;
        this.appUsers = appUsers;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        // TODO - I am not sure, if this is the right place.
        try {
            CsldUser existing = personService.getByEmail(validatable.getValue());
            CsldUser loggedIn = appUsers.getLoggedUser();

            if(!updateExisting && (existing != null)) {
                error(validatable, "person-exists");
            }

            if(updateExisting && existing != null && loggedIn != null && !existing.getId().equals(loggedIn.getId())) {
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
