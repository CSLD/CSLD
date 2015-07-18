package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.News;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Generic Dao for News.
 */
@Repository
public class NewsDAO extends GenericHibernateDAO<News, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<News>(News.class);
    }

    public List<News> getLastNews(int showInPanel) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .addOrder(Order.desc("added"))
                .setMaxResults(showInPanel);
        return crit.list();
    }

    public List<News> allForUser(Integer userId) {
        Criteria crit = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession())
                .addOrder(Order.desc("added"))
                .add(Restrictions.eq("author.id", userId));

        return crit.list();
    }
}
