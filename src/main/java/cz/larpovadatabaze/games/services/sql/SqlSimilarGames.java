package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.SimilarGame;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.SimilarGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SqlSimilarGames extends CRUD<SimilarGame, Integer> implements SimilarGames {
    private SessionFactory sessionFactory;
    private GenericHibernateDAO<Game, Integer> games;

    @Autowired
    public SqlSimilarGames(SessionFactory sessionFactory, AppUsers appUsers) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(SimilarGame.class)));

        this.sessionFactory = sessionFactory;
        this.games = new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers));
    }

    @Override
    public List<Game> allForGame(Game game) {
        Session session = sessionFactory.getCurrentSession();

        List<Integer> similarGames = session.createQuery(
                "select similarGame.idGame2 from SimilarGame similarGame " +
                        "where similarGame.idGame1 = :id " +
                        "order by similarGame.similarity")
                .setInteger("id", game.getId()).list();

        List<Game> results = new ArrayList<>();
        for (Integer gameId : similarGames) {
            results.add(games.findById(gameId));
        }

        return results;
    }

    @Override
    public void recalculateForAll() {
        throw new NotImplementedException("Not yet");
    }
}
