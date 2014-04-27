package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GameBuilder;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.utils.Strings;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 *
 */
@Repository
public class GameDAO extends GenericHibernateDAO<Game, Integer> {
    @Qualifier("sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Used when autoCompletable field is used.
     *
     * @param gameName Expected format is {Name} Name is unique identifier of game.
     * @return It should return only single game or no game if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        Session session = sessionFactory.getCurrentSession();
        Criteria uniqueGame = new GameBuilder(session).build()
                .add(Restrictions.eq("name", gameName));
        return uniqueGame.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getLastGames(int amountOfGames) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .setMaxResults(amountOfGames)
                .addOrder(Order.desc("added"));

        return criteria.list();
    }

    public int getAmountOfGames() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    public Game getRandomGame() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .add(Restrictions.sqlRestriction("random() < 0.01"))
                .setMaxResults(1);

        Game result = (Game) criteria.uniqueResult();
        if(result == null) {
            return getRandomGame();
        } else {
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(first)
                .setMaxResults(count);

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(first)
                .setMaxResults(count);

        return criteria.list();
    }

    public long getAmountOfGamesOfAuthor(CsldUser author) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long)criteria.uniqueResult();
    }

    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long)criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getSimilar(Game game) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .add(Restrictions.sqlRestriction("csld_is_similar('" + game.getId() + "', this_.id)"))
                .addOrder(Order.desc("totalRating"))
                .setMaxResults(5);

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getFilteredGames(FilterGame filterGame, List<Label> labels, int first, int count, Order orderBy){
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build();
        if(labels.size() > 0){
            criteria.add(Restrictions.in("labels", labels));
        }
        if (filterGame != null) {
            if(filterGame.getMinDays() != 0){
                criteria.add(Restrictions.ge("days", filterGame.getMinDays()));
            }
            if(filterGame.getMinHours() != 0){
                criteria.add(Restrictions.ge("hours", filterGame.getMinHours()));
            }
            if(filterGame.getMinPlayers() != 0){
                criteria.add(Restrictions.ge("players", filterGame.getMinPlayers()));
            }

            if(filterGame.getMaxDays() != null){
                criteria.add(Restrictions.le("days",filterGame.getMaxDays()));
            }
            if(filterGame.getMaxHours() != null){
                criteria.add(Restrictions.le("hours",filterGame.getMaxHours()));
            }
            if(filterGame.getMaxPlayers() != null){
                criteria.add(Restrictions.le("players",filterGame.getMaxPlayers()));
            }
        }
        if (orderBy != null) {
            criteria.addOrder(orderBy);
        }
        if (count > 0) {
            criteria.setFirstResult(first)
                    .setMaxResults(count);
        }

        return criteria.list();
    }

    public long getAmountOfFilteredGames(FilterGame filterGame, List<Label> labels) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build();
        if(labels.size() > 0){
            criteria.add(Restrictions.in("labels", labels));
        }
        if (filterGame != null) {
            if(filterGame.getMinDays() != 0){
                criteria.add(Restrictions.ge("days", filterGame.getMinDays()));
            }
            if(filterGame.getMinHours() != 0){
                criteria.add(Restrictions.ge("hours", filterGame.getMinHours()));
            }
            if(filterGame.getMinPlayers() != 0){
                criteria.add(Restrictions.ge("players", filterGame.getMinPlayers()));
            }

            if(filterGame.getMaxDays() != null){
                criteria.add(Restrictions.le("days",filterGame.getMaxDays()));
            }
            if(filterGame.getMaxHours() != null){
                criteria.add(Restrictions.le("hours",filterGame.getMaxHours()));
            }
            if(filterGame.getMaxPlayers() != null){
                criteria.add(Restrictions.le("players",filterGame.getMaxPlayers()));
            }
        }
        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public boolean saveOrUpdate(Game entity) {
        try{
            Session session = sessionFactory.getCurrentSession();
            session.saveOrUpdate(entity);
            flush();
            return true;
        } catch (HibernateException ex){
            ex.printStackTrace();
        }

        try{
            Session session = sessionFactory.getCurrentSession();
            Game item2 = (Game) session.get(Game.class, entity.getId());
            Game item3 = (Game) session.merge(entity);
            flush();
            return true;
        } catch (HibernateException ex){
            ex.printStackTrace();
            return false;
        }
    }
}
