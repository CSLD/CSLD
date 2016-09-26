package cz.larpovadatabaze.dao.builder;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.utils.UserUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class GameBuilder implements IBuilder {
    DetachedCriteria baseCriteria;

    public GameBuilder() {
        baseCriteria = DetachedCriteria.forClass(Game.class, "game");
        withDeletedRestriction();
    }

    /**
     * Add restriction that shows deleted games only to editors and admins.
     * This one is added by default. So be careful when using it.
     */
    public void withDeletedRestriction(){
        if(!UserUtils.isEditor()){
            // Only games that were not deleted will be shown.
            baseCriteria.add(Restrictions.eqOrIsNull("deleted", false));
        }
    }

    public DetachedCriteria build(){
        return baseCriteria;
    }

    @Override
    public Class getClassSpecific() {
        return Game.class;
    }
}
