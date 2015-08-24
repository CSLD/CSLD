package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.UserPlayedGame;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class UserPlayedGameDAO extends GenericHibernateDAO<UserPlayedGame, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<UserPlayedGame>(UserPlayedGame.class);
    }

    public UserPlayedGame getUserPlayedGame(int gameId, int userId) {
        Criteria stateOfGame = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .add(Restrictions.eq("game.id", gameId))
                .add(Restrictions.eq("playerOfGame.id", userId));
        return (UserPlayedGame) stateOfGame.uniqueResult();
    }
}
