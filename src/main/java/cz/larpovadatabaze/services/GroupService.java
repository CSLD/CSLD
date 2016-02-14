package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface GroupService extends GenericService<CsldGroup>, IIconReferenceProvider<CsldGroup> {
    boolean insert(CsldGroup group);

    CsldGroup getById(Integer id);

    List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException;

    void saveOrUpdate(CsldGroup group);
}
