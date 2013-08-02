package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.PersonService;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 14.6.13
 * Time: 8:28
 */
public class UniqueUserValidator implements IValidator<String> {
    PersonService personService;

    private boolean updateExisting;

    public UniqueUserValidator(boolean updateExisting, PersonService personService){
        this.personService = personService;
        this.updateExisting = updateExisting;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        Person example = new Person();
        example.setEmail(validatable.getValue());
        List<Person> existingPerson = personService.getUnique(example);
        if(!updateExisting && existingPerson.size() > 0) {
            error(validatable, "This person already exists.");
        }
        if(updateExisting && existingPerson.size() == 0){
            error(validatable, "User you are trying to edit does not exist.");
        }
    }

    private void error(IValidatable<String> validatable, String message) {
        ValidationError error = new ValidationError();
        error.setMessage(message);
        validatable.error(error);
    }
}
