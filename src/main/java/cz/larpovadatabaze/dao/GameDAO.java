package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GameBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.models.FilterGame;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The DAO should already return entity, which is translated to current state. How do I do that?
 */
@Repository
public class GameDAO extends GenericHibernateDAO<Game, Integer> {
    /**
     * How old game is considered new
     */
    private final static int YEARS_NEW = 2;

    /**
     * How old game is considered old
     */
    private final static int YEARS_OLD = 5;

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

    @Override
    public List<Game> findAll() {
        Criteria crit = new GameBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());

        crit.setFetchMode("availableLanguages", FetchMode.SELECT);

        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getLastGames(int amountOfGames, List<Locale> locales) {
        Session session = sessionFactory.getCurrentSession();

        DetachedCriteria subQueryCriteria = new GameBuilder().build();

        addLanguageRestriction(subQueryCriteria, locales);

        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session);

        criteria.add(Subqueries.propertyIn("id", subQueryCriteria));

        criteria.setMaxResults(amountOfGames)
                .addOrder(Order.desc("added"));

        criteria.setFetchMode("availableLanguages", FetchMode.SELECT);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getMostPopularGames(int amountOfGames, List<Locale> locales) {
        Session session = sessionFactory.getCurrentSession();

        DetachedCriteria dc = new GameBuilder().build();

        addLanguageRestriction(dc, locales);

        Criteria criteria = dc.getExecutableCriteria(session)
                .setMaxResults(amountOfGames)
                .addOrder(Order.desc("totalRating"));

        return criteria.list();
    }

    public int getAmountOfGames() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .setProjection(Projections.rowCount());

        return ((Long) criteria.uniqueResult()).intValue();
    }

    public Game getRandomGame() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.sqlRestriction("random() < 0.01"))
                .setMaxResults(1);

        Game result = (Game) criteria.uniqueResult();
        if (result == null && getAmountOfGames() > 0) {
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

        return (Long) criteria.uniqueResult();
    }

    public long getAmountOfGamesOfGroup(CsldGroup csldGroup) {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .createAlias("game.groupAuthor", "group")
                .add(Restrictions.eq("group.id", csldGroup.getId()))
                .setProjection(Projections.countDistinct("game.id"));

        return (Long) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Game> getSimilar(Game game) {
        Session session = sessionFactory.getCurrentSession();

        List<Integer> labeledGames = game.getLabels().stream().map(Label::getId).collect(Collectors.toList());
        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.not(Restrictions.eq("id", game.getId())))
                .addOrder(Order.desc("totalRating"))
                .createCriteria("labels")
                .add(Restrictions.in("id", labeledGames))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .setMaxResults(5);

        return criteria.list();
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

        if (!filterGame.getLanguages().isEmpty()) {
            addLanguageRestriction(criteria, filterGame.getLanguages());
        }

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
        Session session = sessionFactory.getCurrentSession();

        DetachedCriteria subQueryCriteria = new GameBuilder().build();

        applyGameFilter(subQueryCriteria, filterGame);

        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = new GameBuilder().build().getExecutableCriteria(session);

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

        DetachedCriteria dc = new GameBuilder().build();

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
            ex.printStackTrace();
        }

        try {
            Session session = sessionFactory.getCurrentSession();
            session.get(Game.class, entity.getId());
            session.merge(entity);
            flush();
            return true;
        } catch (HibernateException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void addLanguageRestriction(DetachedCriteria criteria, List<Locale> languages) {
        criteria
                .createCriteria("availableLanguages")
                .add(Restrictions.in("language", languages.stream().map(Locale::getLanguage).collect(Collectors
                        .toList())));
    }
}
