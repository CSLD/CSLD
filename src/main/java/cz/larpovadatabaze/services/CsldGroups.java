package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;

import java.util.List;

/**
 *
 */
public interface CsldGroups extends CRUDService<CsldGroup, Integer>, IIconReferenceProvider<CsldGroup> {
    List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException;
}
