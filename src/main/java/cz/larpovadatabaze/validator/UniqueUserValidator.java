package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.List;

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
        CsldUser example = new CsldUser();
        example.setPerson(new Person());
        example.getPerson().setEmail(validatable.getValue());
        List<CsldUser> existingPerson = personService.getUnique(example);
        if(!updateExisting && existingPerson.size() > 0) {
            error(validatable, "person-exists");
        }
        if(updateExisting && existingPerson.size() == 0){
            error(validatable, "update-nonexistent");
        }
    }

    private void error(IValidatable<String> validatable, String message) {
        ValidationError error = new ValidationError();
        error.setMessage(message);
        validatable.error(error);
    }
}
