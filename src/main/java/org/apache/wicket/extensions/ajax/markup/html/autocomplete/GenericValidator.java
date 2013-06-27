package org.apache.wicket.extensions.ajax.markup.html.autocomplete;

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
public class GenericValidator<T> implements INullAcceptingValidator<T>, Serializable {
    protected GenericService<T> service;

    public GenericValidator(GenericService<T> service){
        this.service = service;
    }

    @Override
    public void validate(IValidatable<T> validatable) {
        T validatableEntity = validatable.getValue();
        if(validatableEntity == null) {
            error(validatable, "You are trying to add nonexisting Entity.");
            return;
        }
        List<T> resultEntitites = service.getUnique(validatableEntity);
        if(resultEntitites.size() == 1) {
            // It is valid
        } else {
            error(validatable, "You are trying to add nonexisting Entity.");
        }
    }

    private void error(IValidatable<T> validatable, String message) {
        ValidationError error = new ValidationError();
        error.setMessage(message);
        validatable.error(error);
    }
}
