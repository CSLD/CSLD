package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.GroupHasMember;

/**
 *
 */
public interface GroupHasMemberService extends GenericService<GroupHasMember>  {
    public void saveOrUpdate(GroupHasMember memberOfGroup);

    void removeAllMembersOfGroup(CsldGroup group);
}
