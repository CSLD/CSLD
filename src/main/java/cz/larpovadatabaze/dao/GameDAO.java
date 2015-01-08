package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GameBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.*;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 */
@Repository
public class GameDAO extends GenericHibernateDAO<Game, Integer> {
    @Qualifier("sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public IBuilder getBuilder() {
        return new GameBuilder();
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
        Criteria uniqueGame = new GameBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.eq("name", gameName));
        return uniqueGame.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getLastGames(int amountOfGames, List<Locale> locales) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .setMaxResults(amountOfGames)
                .addOrder(Order.desc("added"));

        addLanguageRestriction(criteria, locales);

        return criteria.list();
    }

    public int getAmountOfGames() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    public Game getRandomGame() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.sqlRestriction("random() < 0.01"))
                .setMaxResults(1);

        Game result = (Game) criteria.uniqueResult();
        if(result == null && getAmountOfGames() > 0) {
            return getRandomGame();
        } else {
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
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
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(first)
                .setMaxResults(count);

        return criteria.list();
    }

    public long getAmountOfGamesOfAuthor(CsldUser author) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long)criteria.uniqueResult();
    }

    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long)criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getSimilar(Game game) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.sqlRestriction("csld_is_similar('" + game.getId() + "', this_.id)"))
                .addOrder(Order.desc("totalRating"))
                .setMaxResults(5);

        List<Game> similarGames  = criteria.list();
        similarGames.remove(game);
        return similarGames;
    }

    /**
     * It returns all games, which were commented by single user. The games are distinct.
     *
     * @param userId Id of the user, whose games we want to get.
     * @return List of games this user commented.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getGamesCommentedByUser(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .createAlias("game.comments", "comments")
                .add(Restrictions.eq("comments.userId", userId));

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getFilteredGames(FilterGame filterGame, List<Label> labels, int first, int count, Order orderBy){
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session);
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

            if(filterGame.getLanguage() != null) {
                List<Locale> languages = new ArrayList<Locale>();
                languages.add(filterGame.getLanguage());
                addLanguageRestriction(criteria, languages);
            }
        }
        if (orderBy != null) {
            criteria.addOrder(orderBy);
        }
        if (count > 0) {
            criteria.setFirstResult(first)
                    .setMaxResults(count);
        }
        if(labels.size() > 0){
            Integer[] labelIds = new Integer[labels.size()];
            for(int i = 0; i < labels.size(); i++) {
                labelIds[i] = labels.get(i).getId();
            }
            criteria.createAlias("game.labels","labels");
            criteria.add(Restrictions.in("labels.id", labelIds));
        }

        return criteria.list();
    }

    public long getAmountOfFilteredGames(FilterGame filterGame, List<Label> labels) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session);
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

            if(filterGame.getLanguage() != null) {
                List<Locale> languages = new ArrayList<Locale>();
                languages.add(filterGame.getLanguage());
                addLanguageRestriction(criteria, languages);
            }
        }
        criteria.setProjection(Projections.rowCount());
        if(labels.size() > 0){
            Integer[] labelIds = new Integer[labels.size()];
            for(int i = 0; i < labels.size(); i++) {
                labelIds[i] = labels.get(i).getId();
            }
            criteria.createAlias("game.labels","labels");
            criteria.add(Restrictions.in("labels.id", labelIds));
        }

        return (Long) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesRatedByUser(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .createAlias("game.ratings", "ratings")
                .add(Restrictions.eq("ratings.userId", userId));

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
            session.get(Game.class, entity.getId());
            session.merge(entity);
            flush();
            return true;
        } catch (HibernateException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void deleteTranslation(Game toModify, Language convertedInput) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GameHasLanguages.class);
        criteria.add(Restrictions.eq("game.id",toModify.getId()));
        criteria.add(Restrictions.eq("language", convertedInput));
        GameHasLanguages lang = (GameHasLanguages) criteria.uniqueResult();

        sessionFactory.getCurrentSession().delete(lang);
        sessionFactory.getCurrentSession().flush();
    }

    private void addLanguageRestriction(Criteria criteria, List<Locale> languages) {
        criteria
                .createCriteria("availableLanguages")
                .createCriteria("language")
                .add(Restrictions.in("language", languages));
    }
}
