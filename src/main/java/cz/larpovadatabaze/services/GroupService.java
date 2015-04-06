package cz.larpovadatabaze.services;

import java.util.List;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;

/**
 *
 */
public interface GroupService extends GenericService<CsldGroup>, IIconReferenceProvider<CsldGroup> {
    public boolean insert(CsldGroup group);

    List<CsldGroup> orderedByName(long first, long amountPerPage);

    List<CsldGroup> orderedByGameCountDesc(long first, long amountPerPage);

    CsldGroup getById(Integer id);

    List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException;

    void saveOrUpdate(CsldGroup group);

    int getAmountOfGroups();

    int getAverageOfGroup(CsldGroup group);
}
