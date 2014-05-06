package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Rating;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class RatingDAO extends GenericHibernateDAO<Rating, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Rating>(Rating.class);
    }

    public int getAmountOfRatings() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .setProjection(Projections.rowCount());

        return ((Long)criteria.uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Rating> getRatingsOfUser(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = getBuilder().build().getExecutableCriteria(session)
                .add(Restrictions.eq("userId", id));

        return criteria.list();
    }

    public double getAverageRating() {
        Session session = sessionFactory.getCurrentSession();
        String sqlQuery = String.format("select csld_count_average()");
        Query query = session.createSQLQuery(sqlQuery);
        if (query.uniqueResult() == null) { return 0.0; }
        else { return (Double) query.uniqueResult(); }
    }
}
