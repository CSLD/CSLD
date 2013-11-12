package org.apache.wicket.extensions.ajax.markup.html.autocomplete;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.services.GenericService;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 14:23
 */
public class GenericValidator<T extends Identifiable> implements INullAcceptingValidator<T>, Serializable {
    protected GenericService<T> service;

    List list = null;
    T object = null;

    boolean required = false;

    public GenericValidator(GenericService<T> service){
        this.service = service;
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        // If validatableEntity is null but the input is not null.

        T validatableEntity = validatable.getValue();
        if(list == null){
            if(object == null && validatableEntity == null){
                if(required)
                    error(validatable, "NonExistingEntity"); //TODO get text from resource file
                return;
            }
            if(validatableEntity == null) {
                validatableEntity = object;
            }
        }
        else if((list.size() < 2) && validatableEntity == null){
            if(required)
                errorNonExisting(validatable);
            return;
        }

        if(validatableEntity != null && validatableEntity.getId() !=null &&
                ((Integer) validatableEntity.getId()) == -1) {
            errorNonExisting(validatable);
            return;
        }

        if(validatableEntity != null){
            List<T> resultEntitites = service.getUnique(validatableEntity);
            if(resultEntitites.size() == 1) {
                // It is valid
            } else if(resultEntitites.size() > 1) {
                error(validatable, "error.ThereAreMoreEntitiesWithSameName"); //TODO get text from resource file
            } else if(resultEntitites.size() == 0) {
                // The entity does not exists and input object is null (not empty)
                errorNonExisting(validatable);
            } else if ((list == null || list.isEmpty())) {
                errorNonExisting(validatable);
            }
        }
    }

    private void errorNonExisting(IValidatable<T> validatable){
        if (required)
            error(validatable, "error.ExistingEntityRequired"); //TODO get text from resource file
        else
            error(validatable, "error.nonexistingEntity"); //TODO get text from resource file
    }


    public void setList(List list){
        this.list = list;
    }

    public void setAutocompleteObject(T object){
        this.object = object;
    }

    public void setRequired(boolean isRequired){
        this.required = isRequired;
    }

    private void error(IValidatable<T> validatable, String message) {
        ValidationError error = new ValidationError();
        error.addKey(message);
        validatable.error(error);
    }
}