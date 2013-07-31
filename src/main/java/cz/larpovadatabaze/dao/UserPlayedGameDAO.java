package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.entities.UserPlayedGamePK;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class UserPlayedGameDAO extends GenericHibernateDAO<UserPlayedGame, UserPlayedGamePK> {
    public UserPlayedGame getUserPlayedGame(int gameId, int userId) {
        Criteria stateOfGame = sessionFactory.getCurrentSession().createCriteria(UserPlayedGame.class)
                .add(Restrictions.eq("gameId", gameId))
                .add(Restrictions.eq("userId", userId));
        return (UserPlayedGame) stateOfGame.uniqueResult();
    }
}
