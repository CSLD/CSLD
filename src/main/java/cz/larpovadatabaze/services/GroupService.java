package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface GroupService extends GenericService<CsldGroup> {
    public boolean insert(CsldGroup group);

    List<CsldGroup> orderedByName();

    CsldGroup getById(Integer id);

    List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException;

    void saveOrUpdate(CsldGroup group);
}
