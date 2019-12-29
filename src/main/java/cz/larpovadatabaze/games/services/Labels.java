package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.exceptions.WrongParameterException;
import cz.larpovadatabaze.common.services.CRUDService;

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
