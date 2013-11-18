package cz.larpovadatabaze.validator;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
public class UniqueGroupValidator implements IValidator<String> {
    @Autowired
    GroupService groupService;
    private boolean updateExisting = false;

    @Override
    public void validate(IValidatable<String> validatable) {
        CsldGroup example = new CsldGroup();
        example.setName(validatable.getValue());
        List<CsldGroup> existingPerson = groupService.getUnique(example);
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

    public void setUpdateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
    }
}
