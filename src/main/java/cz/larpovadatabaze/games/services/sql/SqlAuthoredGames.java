package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.AuthoredGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class SqlAuthoredGames implements AuthoredGames {
    private GenericHibernateDAO<Game, Integer> games;

    @Autowired
    public SqlAuthoredGames(SessionFactory sessionFactory, AppUsers appUsers) {
        this.games = new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers));
    }

    @Override
    public List<Game> gamesOfAuthors(Game game) {
        // TODO sort games by rating.
        Set<Game> games = new LinkedHashSet<>();
        for (CsldUser author : game.getAuthors()) {
            games.addAll(author.getAuthorOf());
        }
        List<Game> gamesOfAuthors = new ArrayList<>(games);
        gamesOfAuthors.sort((o1, o2) -> {
            if (o1.getTotalRating().equals(o2.getTotalRating())) {
                return 0;
            }
            return o1.getTotalRating() < o2.getTotalRating() ? -1 : 1;
        });
        return gamesOfAuthors;
    }

    @Override
    public Collection<Game> getGamesOfAuthor(CsldUser author, int offset, int limit) {
        Criteria criteria = games.getExecutableCriteria()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(offset)
                .setMaxResults(limit);

        return new LinkedHashSet<>(criteria.list());
    }

    @Override
    public Collection<Game> getGamesOfGroup(CsldGroup csldGroup, int offset, int limit) {
        Criteria criteria = games.getExecutableCriteria()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(offset)
                .setMaxResults(limit);

        return new LinkedHashSet<>(criteria.list());
    }

    @Override
    public long getAmountOfGamesOfAuthor(CsldUser author) {
        Criteria criteria = games.getExecutableCriteria()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long) criteria.uniqueResult();
    }

    @Override
    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        Criteria criteria = games.getExecutableCriteria()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long) criteria.uniqueResult();
    }
}
