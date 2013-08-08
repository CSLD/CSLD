package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
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


    public List<Game> getRated() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Game game order by csld_count_rating(game.id) desc");
        List<Game> allGames = query.list();
        return allGames;
    }

    public List<Game> getOrderedByName() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Game game order by name");
        List<Game> allGames = query.list();
        return allGames;
    }

    /**
     * It returns Games sorted by amount of Ratings.
     *
     * @return
     */
    public List<Game> getRatedAmount() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Game game");
        List<Game> allGames = query.list();

        Collections.sort(allGames, new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                int game1Ratings = o1.getRatings().size();
                int game2Ratings = o2.getRatings().size();
                if(game1Ratings < game2Ratings) {
                    return 1;
                } else if(game1Ratings == game2Ratings) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return allGames;
    }

    /**
     * It returns games sorted by the amount of Comments.
     *
     * @return games sorted by amount of comments.
     */
    public List<Game> getCommentedAmount() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("from Game game");
        List<Game> allGames = query.list();

        Collections.sort(allGames, new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                int game1Comments = o1.getComments().size();
                int game2Comments = o2.getComments().size();
                if(game1Comments < game2Comments) {
                    return 1;
                } else if(game1Comments == game2Comments) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return allGames;
    }

    /**
     * It returns rating of the game. Only place where rating is counted is in the database function csld_count_rating
     *
     * @param game
     * @return If game has no rating, it returns 0
     */
    @SuppressWarnings("unchecked")
    public double getRatingOfGame(Game game) {
        Session session = sessionFactory.getCurrentSession();
        String sqlQuery = String.format("select csld_count_rating(%s)", game.getId());
        Query query = session.createSQLQuery(sqlQuery);
        return (Double)query.uniqueResult();
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param gameName Expected format is {Name} Name is unique identifier of game.
     * @return It should return only single game or no game if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getByAutoCompletable(String gameName) throws WrongParameterException {
        Criteria uniqueGame = sessionFactory.getCurrentSession().createCriteria(Game.class).add(
                Restrictions.eq("form.wholeName", gameName)
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
        List<Game> gamesSortedByRating = getRated();
        for(Game game: gamesSortedByRating) {
            if(game.getAuthors().contains(actualAuthor)){
                return game;
            }
        }
        throw new RuntimeException("Trying to get Best Game of someone who is not author.");
    }
}
