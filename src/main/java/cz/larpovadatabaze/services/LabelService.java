package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface LabelService extends GenericService<Label>{
    public List<Label> getRequired();

    public List<Label> getOptional();

    void update(Label label);

    List<Label> getByAutoCompletable(String labelName) throws WrongParameterException;

    boolean saveOrUpdate(Label label);
}
