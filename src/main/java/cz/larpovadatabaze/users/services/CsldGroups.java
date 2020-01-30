package cz.larpovadatabaze.users.services;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.services.CRUDService;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;

/**
 *
 */
public interface CsldGroups extends CRUDService<CsldGroup, Integer>, IIconReferenceProvider<CsldGroup> {
}
