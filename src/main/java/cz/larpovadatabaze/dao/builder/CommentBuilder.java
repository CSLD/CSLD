package cz.larpovadatabaze.dao.builder;

import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class CommentBuilder implements IBuilder {
    DetachedCriteria baseCriteria;

    public CommentBuilder() {
        baseCriteria = DetachedCriteria.forClass(Comment.class, "comment");
        withDeletedRestriction();
    }

    /**
     * Add restriction that shows deleted games only to editors and admins.
     * This one is added by default. So be careful when using it.
     */
    public void withDeletedRestriction(){
        CsldUser loggedUser = CsldAuthenticatedWebSession.get().getLoggedUser();
        if(loggedUser == null || loggedUser.getRole() < CsldRoles.getRoleByName("Editor")){
            // Only games that were not deleted will be shown.
            baseCriteria.createAlias("comment.game", "game")
                    .add(Restrictions.eq("game.deleted", false));
        }
    }

    public DetachedCriteria build(){
        return baseCriteria;
    }
}
