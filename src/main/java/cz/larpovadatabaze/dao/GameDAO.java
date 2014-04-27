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
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.3.13
 * Time: 12:45
 */
@Repository
public class GameDAO extends GenericHibernateDAO<Game, Integer> {
    @Qualifier("sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;
    /*
    Is it possible to create some kind of builder, as many of these queries are basically similar. There is also another
    question. Is it possible to write the same queries using just one type of API without switching between them?
    Probably the best choice would be to try Hibernates Criteria API.
     */

    /**
     * It returns part of the games ordered by rating. Only given amount with given offset is returned.
     *
     * @param first Position of first game to be returned.
     * @param amountPerPage Amount of games to be retrieved.
     *
     * @return List of games, which contains between zero and amountPerPage data.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getRated(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build()
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue())
                .addOrder(Order.desc("totalRating"));

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getOrderedByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .addOrder(Order.asc("name"))
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue());

        return criteria.list();
    }

    /**
     * It returns Games sorted by amount of Ratings.
     *
     * @return All games sorted by amount of Ratings
     */
    @SuppressWarnings("unchecked")
    public List<Game> getRatedAmount(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue())
                .addOrder(Order.desc("amountOfRatings"));

        return criteria.list();
    }

    /**
     * It returns games sorted by the amount of Comments.
     *
     * @return games sorted by amount of comments.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getCommentedAmount(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder(session).build()
                .setFirstResult(first.intValue())
                .setMaxResults(amountPerPage.intValue())
                .addOrder(Order.desc("amountOfComments"));

        return criteria.list();
    }

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

    /**
     * It returns best game of given author.
     *
     * @param actualAuthor Any of CsldUser that created at least one game.
     * @return Best Game, Every author has at least one game as definition.
     */
    public Game getBestGame(CsldUser actualAuthor) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder(session).build()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", actualAuthor.getId()))
                .addOrder(Order.desc("totalRating"));

        return (Game) criteria.uniqueResult();
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

    @SuppressWarnings("unchecked")
    public List<Game> getFilteredGames(FilterGame filterGame, List<Label> labels, int offset, int limit, String orderBy){
        Session session = sessionFactory.getCurrentSession();
        String labelSql = "";
        // Something like in http://stackoverflow.com/questions/7344701/hibernate-criteria-equivalent-for-in-clause-in-subqueries
        if(labels.size() > 0){
            labelSql = "ARRAY[";
            for(Label label: labels){
                labelSql += label.getId() + ",";
            }
            labelSql = Strings.removeLast(labelSql);
            labelSql += "] <@ (select array(select id_label from csld_game_has_label where id_game = game.id)) ";
        } else {
            labelSql = "1=1 ";
        }
        String sqlForGameIds = String.format("select " +
                "game.id, game.name, game.description, game.year, game.web, game.hours," +
                "game.days, game.players, game.men_role, game.women_role, game.both_role," +
                "game.added, game.average_rating, game.total_rating, game.amount_of_comments, game.amount_of_played, " +
                "game.amount_of_ratings, game.added_by, game.video, game.image, game.gallery_url, game.photo_author " +
                "from csld_game game where 1=1 " +
                "and %s",
                labelSql);

        if (filterGame != null) {
            if(filterGame.getMinDays() != 0){
                sqlForGameIds += " and game.days >= " + filterGame.getMinDays();
            }
            if(filterGame.getMinHours() != 0){
                sqlForGameIds += " and game.hours >= " + filterGame.getMinHours();
            }
            if(filterGame.getMinPlayers() != 0){
                sqlForGameIds += " and game.players >= " + filterGame.getMinPlayers();
            }

            if(filterGame.getMaxDays() != null){
                sqlForGameIds += " and game.days <= " + filterGame.getMaxDays();
            }
            if(filterGame.getMaxHours() != null){
                sqlForGameIds += " and game.hours <= " + filterGame.getMaxHours();
            }
            if(filterGame.getMaxPlayers() != null){
                sqlForGameIds += " and game.players <= " + filterGame.getMaxPlayers();
            }
        }
        if (orderBy != null) sqlForGameIds += orderBy;
        if (limit > 0) {
            sqlForGameIds += " offset " + offset + " limit " + limit;
        }

        Query query = session.createSQLQuery(sqlForGameIds).addEntity(Game.class);
        return query.list();
    }

    public long getAmountOfFilteredGames(FilterGame filterGame, List<Label> labels) {
        Session session = sessionFactory.getCurrentSession();
        String labelSql = "";
        if(labels.size() > 0){
            labelSql = "ARRAY[";
            for(Label label: labels){
                labelSql += label.getId() + ",";
            }
            labelSql = Strings.removeLast(labelSql);
            labelSql += "] <@ (select array(select id_label from csld_game_has_label where id_game = game.id)) ";
        } else {
            labelSql = "1=1 ";
        }
        String sqlForGameIds = String.format("select count(distinct game.id) " +
                "from csld_game game join csld_game_has_label gha on game.id = gha.id_game " +
                "where 1=1 and %s",
                labelSql);
        if(filterGame.getMinDays() != 0){
            sqlForGameIds += " and game.days >= " + filterGame.getMinDays();
        }
        if(filterGame.getMinHours() != 0){
            sqlForGameIds += " and game.hours >= " + filterGame.getMinHours();
        }
        if(filterGame.getMinPlayers() != 0){
            sqlForGameIds += " and game.players >= " + filterGame.getMinPlayers();
        }

        if(filterGame.getMaxDays() != null){
            sqlForGameIds += " and game.days <= " + filterGame.getMaxDays();
        }
        if(filterGame.getMaxHours() != null){
            sqlForGameIds += " and game.hours <= " + filterGame.getMaxHours();
        }
        if(filterGame.getMaxPlayers() != null){
            sqlForGameIds += " and game.players <= " + filterGame.getMaxPlayers();
        }

        Query query = session.createSQLQuery(sqlForGameIds);
        return ((BigInteger) query.uniqueResult()).longValue();
    }
}
