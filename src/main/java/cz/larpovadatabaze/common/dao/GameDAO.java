package cz.larpovadatabaze.common.dao;

import cz.larpovadatabaze.common.api.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.models.FilterGame;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
@Repository
public class GameDAO extends GenericHibernateDAO<Game, Integer> {
    private final static Logger logger = Logger.getLogger(GameDAO.class);
    /**
     * How old game is considered new
     */
    private final static int YEARS_NEW = 2;

    /**
     * How old game is considered old
     */
    private final static int YEARS_OLD = 5;

    @Autowired
    public GameDAO(SessionFactory sessionFactory, AppUsers appUsers) {
        super(sessionFactory, new GameBuilder(appUsers));
    }

    /**
     * Used when autoCompletable field is used.
     *
     * @param gameName Expected format is {Name} Name is unique identifier of game.
     * @return It should return only single game or no game if none belongs to given data.
     */
    @SuppressWarnings("unchecked")
    public List<Game> getByAutoCompletable(String gameName) {
        Criteria uniqueGame = getExecutableCriteria()
                .add(Restrictions.eq("name", gameName));
        return uniqueGame.list();
    }

    @Override
    public List<Game> findAll() {
        Criteria crit = getExecutableCriteria();

        crit.setFetchMode("availableLanguages", FetchMode.JOIN);

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getLastGames(int amountOfGames) {
        Session session = sessionFactory.getCurrentSession();

        DetachedCriteria subQueryCriteria = getBuilder().build();

        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = getExecutableCriteria();

        criteria.add(Subqueries.propertyIn("id", subQueryCriteria));

        criteria.setMaxResults(amountOfGames)
                .addOrder(Order.desc("added"));

        criteria.setFetchMode("availableLanguages", FetchMode.SELECT);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getMostPopularGames(int amountOfGames) {
        Criteria criteria = getExecutableCriteria()
                .setMaxResults(amountOfGames)
                .addOrder(Order.desc("totalRating"));

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesOfAuthor(CsldUser author, int first, int count) {
        Criteria criteria = getExecutableCriteria()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(first)
                .setMaxResults(count);

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesOfGroup(CsldGroup csldGroup, int first, int count) {
        Criteria criteria = getExecutableCriteria()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .addOrder(Order.asc("totalRating"))
                .setFirstResult(first)
                .setMaxResults(count);

        return criteria.list();
    }

    public long getAmountOfGamesOfAuthor(CsldUser author) {
        Criteria criteria = getExecutableCriteria()
                .createAlias("game.authors", "author")
                .add(Restrictions.eq("author.id", author.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long) criteria.uniqueResult();
    }

    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        Criteria criteria = getExecutableCriteria()
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getSimilar(Game game) {
        Session session = sessionFactory.getCurrentSession();

        List<Integer> similarGames = session.createQuery("select similarGame.idGame2 from SimilarGame similarGame where similarGame.idGame1 = :id order by similarGame.similarity")
                .setInteger("id", game.getId()).list();

        List<Game> results = new ArrayList<>();
        for(Integer gameId: similarGames) {
               results.add(findById(gameId));
        }

        return results;
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
                .add(Restrictions.eq("comments.user.id", userId));

        return criteria.list();
    }

    private Integer[] getLabelIds(List<Label> labels) {
        return labels.stream().map(Label::getId).toArray(Integer[]::new);
    }

    private void addLabelCriteria(DetachedCriteria criteria, List<Label> labels) {
        if (!labels.isEmpty()) {
            DetachedCriteria dc = DetachedCriteria.forClass(Game.class, "game2");
            dc.createAlias("game2.labels", "labels");
            dc.add(Restrictions.in("labels.id", getLabelIds(labels)));
            dc.add(Restrictions.eqProperty("game2.labels", "game.id"));
            dc.setProjection(Projections.id());
            criteria.add(Subqueries.exists(dc));
        }
    }

    /**
     * Applies filter in FilterGame object to the criteria. Order is not affected.
     *
     * @param criteria   Criteria to affect
     * @param filterGame Filter to use
     */
    private void applyGameFilter(DetachedCriteria criteria, FilterGame filterGame) {
        addLabelCriteria(criteria, filterGame.getRequiredLabels());
        addLabelCriteria(criteria, filterGame.getOtherLabels());

        Integer yearLimit = null;
        if (filterGame.isShowArchived()) {
            yearLimit = null;
        } else {
            if (filterGame.isShowOnlyNew()) {
                // Show only games, which last comment is younger than YEARS_NEW
                yearLimit = YEARS_NEW;
            } else {
                // Show only games, which last comment is older than.
                yearLimit = YEARS_OLD;
            }
        }

        if (yearLimit != null) {
            // Show games from this & last year
            criteria.createCriteria("comments", "comment", JoinType.LEFT_OUTER_JOIN);
            Calendar oldestRelevantComment = Calendar.getInstance();
            oldestRelevantComment.add(Calendar.YEAR, -yearLimit);
            criteria.add(Restrictions.or(
                    Restrictions.gt("year", oldestRelevantComment.get(Calendar.YEAR)),
                    Restrictions.gt("comment.added", oldestRelevantComment.getTime())));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Game> getFilteredGames(FilterGame filterGame, int first, int count) {
        DetachedCriteria subQueryCriteria = getBuilder().build();

        applyGameFilter(subQueryCriteria, filterGame);

        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = getExecutableCriteria();

        criteria.add(Subqueries.propertyIn("id", subQueryCriteria));

        switch (filterGame.getOrderBy()) {
            case ADDED_DESC:
                criteria.addOrder(Order.desc("added"));
                break;
            case NUM_COMMENTS_DESC:
                criteria.addOrder(Order.desc("amountOfComments"));
                break;
            case NUM_RATINGS_DESC:
                criteria.addOrder(Order.desc("amountOfRatings"));
                break;
            case RATING_DESC:
                criteria.addOrder(Order.desc("totalRating"));
        }

        criteria.addOrder(Order.desc("id"));

        if (count > 0) {
            criteria.setFirstResult(first)
                    .setMaxResults(count);
        }

        criteria.setFetchMode("availableLanguages", FetchMode.SELECT);
        return criteria.list();
    }

    public long getAmountOfFilteredGames(FilterGame filterGame) {
        Session session = sessionFactory.getCurrentSession();

        DetachedCriteria dc = getBuilder().build();

        applyGameFilter(dc, filterGame);

        Criteria criteria = dc.getExecutableCriteria(session);

        criteria.setProjection(Projections.countDistinct("id"));

        return (Long) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getGamesRatedByUser(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .createAlias("game.ratings", "ratings")
                .add(Restrictions.eq("ratings.user.id", userId));

        return criteria.list();
    }

    @Override
    public boolean saveOrUpdate(Game entity) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.saveOrUpdate(entity);
            flush();
            return true;
        } catch (HibernateException ex) {
            logger.error(ex);
        }

        try {
            Session session = sessionFactory.getCurrentSession();
            session.get(Game.class, entity.getId());
            session.merge(entity);
            flush();
            return true;
        } catch (HibernateException ex) {
            logger.error(ex);
            return false;
        }
    }

    public List<Game> getFirstChoices(String startsWith, int maxChoices) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .setMaxResults(maxChoices)
                .add(Restrictions.ilike("name", "%" + startsWith + "%"));

        return criteria.list();
    }
}
