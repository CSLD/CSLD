package cz.larpovadatabaze.common.dao.builder;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class GameBuilder implements IBuilder {
    private AppUsers appUsers;

    public GameBuilder(AppUsers appUsers) {
        this.appUsers = appUsers;
    }

    /**
     * Add restriction that shows deleted games only to editors and admins.
     * This one is added by default. So be careful when using it.
     */
    public DetachedCriteria build() {
        DetachedCriteria baseCriteria = DetachedCriteria.forClass(Game.class, "game");
        if (!appUsers.isEditor()) {
            // Only games that were not deleted will be shown.
            baseCriteria.add(Restrictions.eqOrIsNull("deleted", false));
        }
        return baseCriteria;
    }

    @Override
    public Class getClassSpecific() {
        return Game.class;
    }
}
