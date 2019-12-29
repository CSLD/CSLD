package cz.larpovadatabaze.users.services;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.exceptions.WrongParameterException;
import cz.larpovadatabaze.common.services.CRUDService;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;

import java.util.List;

/**
 *
 */
public interface CsldGroups extends CRUDService<CsldGroup, Integer>, IIconReferenceProvider<CsldGroup> {
    List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException;
}
