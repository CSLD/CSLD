package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.dao.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GameBuilder;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.games.models.FilterGameDTO;
import cz.larpovadatabaze.games.services.FilteredGames;
import cz.larpovadatabaze.users.services.AppUsers;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Repository
@Transactional
public class SqlFilteredGames implements FilteredGames {
    /**
     * How old game is considered new
     */
    private static final int YEARS_NEW = 2;

    /**
     * How old game is considered old
     */
    private static final int YEARS_OLD = 5;

    private GenericHibernateDAO<Game, Integer> games;

    @Autowired
    public SqlFilteredGames(SessionFactory sessionFactory, AppUsers appUsers) {
        games = new GenericHibernateDAO<>(sessionFactory, new GameBuilder(appUsers));
    }

    @Override
    public List<Game> paginated(FilterGameDTO filter, int offset, int limit) {
        DetachedCriteria subQueryCriteria = games.getBuilder().build();

        applyGameFilter(subQueryCriteria, filter);

        subQueryCriteria.setProjection(Projections.distinct(Projections.id()));

        Criteria criteria = games.getExecutableCriteria();

        criteria.add(Subqueries.propertyIn("id", subQueryCriteria));

        switch (filter.getOrderBy()) {
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

        if (limit > 0) {
            criteria.setFirstResult(offset)
                    .setMaxResults(limit);
        }

        return criteria.list();
    }

    @Override
    public long totalAmount(FilterGameDTO filter) {
        DetachedCriteria dc = games.getBuilder().build();
        applyGameFilter(dc, filter);

        Criteria criteria = games.getExecutableCriteria();
        criteria.setProjection(Projections.countDistinct("id"));

        return (Long) criteria.uniqueResult();
    }

    /**
     * Applies filter in FilterGame object to the criteria. Order is not affected.
     *
     * @param criteria   Criteria to affect
     * @param filterGame Filter to use
     */
    private void applyGameFilter(DetachedCriteria criteria, FilterGameDTO filterGame) {
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

    private Integer[] getLabelIds(List<Label> labels) {
        return labels.stream().map(Label::getId).toArray(Integer[]::new);
    }
}
