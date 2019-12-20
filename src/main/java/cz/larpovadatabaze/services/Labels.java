package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface Labels extends CRUDService<Label, Integer> {
    List<Label> getRequired();

    List<Label> getOptional();

    List<Label> getAuthorizedOptional(CsldUser authorizedTo);

    List<Label> getAuthorizedRequired(CsldUser authorizedTo);

    List<Label> getByAutoCompletable(String labelName) throws WrongParameterException;
}
