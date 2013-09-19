package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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

    @SuppressWarnings("unchecked")
    public List<Game> getRated(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by totalRating desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getRated() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by totalRating desc");
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getOrderedByName(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by name");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    /**
     * It returns Games sorted by amount of Ratings.
     *
     * @return All games sorted by amount of Ratings
     */
    @SuppressWarnings("unchecked")
    public List<Game> getRatedAmount(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by csld_amount_of_ratings(game.id) desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    /**
     * It returns games sorted by the amount of Comments.
     *
     * @return games sorted by amount of comments.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getCommentedAmount(Long first, Long amountPerPage) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by amountOfComments desc");
        query.setFirstResult(first.intValue());
        query.setMaxResults(amountPerPage.intValue());
        return query.list();
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param gameName Expected format is {Name} Name is unique identifier of game.
     * @return It should return only single game or no game if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        Criteria uniqueGame = sessionFactory.getCurrentSession().
                createCriteria(Game.class).add(
                    Restrictions.eq("name", gameName)
        );
        return uniqueGame.list();
    }

    /**
     * It returns best game of given author.
     *
     * @param actualAuthor Any of CsldUser that created at least one game.
     * @return Best Game, Every author has at least one game as definition.
     */
    public Game getBestGame(CsldUser actualAuthor) {
        // TODO Get Best game of author
        List<Game> gamesSortedByRating = getRated();
        for(Game game: gamesSortedByRating) {
            if(game.getAuthors().contains(actualAuthor)){
                return game;
            }
        }
        throw new RuntimeException("Trying to get Best Game of someone who is not author.");
    }

    @SuppressWarnings("unchecked")
    public List<Game> getLastGames(int amountOfGames) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from Game game order by added desc");
        query.setMaxResults(amountOfGames);
        return query.list();
    }

    public int getAmountOfGames() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Game.class);
        criteria.setProjection(Projections.rowCount());
        return ((Long)criteria.uniqueResult()).intValue();
    }

    public Game getRandomGame() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Game.class);
        criteria.add(Restrictions.sqlRestriction("random() < 0.01"));
        criteria.setMaxResults(1);
        return (Game) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getSimilar(Game game) {
        Session session = sessionFactory.getCurrentSession();
        Game example = new Game();
        example.setLabels(game.getLabels());
        Example exampleCrit = Example.create(example);
        Criteria criteria = session.createCriteria(Game.class);
        criteria.add(exampleCrit);
        criteria.add(Restrictions.sqlRestriction("1=1 order by total_rating desc"));
        // Five similar games still looks kind of nice.
        criteria.setMaxResults(5);
        return criteria.list();
    }
}
