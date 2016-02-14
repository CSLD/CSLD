package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.entities.LabelHasLanguages;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface LabelService extends GenericService<Label>{
    List<Label> getRequired();

    List<Label> getOptional();

    void update(Label label);

    List<Label> getAuthorizedOptional(CsldUser authorizedTo);

    List<Label> getAuthorizedRequired(CsldUser authorizedTo);

    List<Label> getByAutoCompletable(String labelName) throws WrongParameterException;

    boolean saveOrUpdate(Label label);

    Label getById(int filterLabel);

    void deleteTranslation(LabelHasLanguages toRemove);
}
