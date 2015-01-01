package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Photo;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class PhotoDAO extends GenericHibernateDAO<Photo, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Photo>(Photo.class);
    }

    public List<Photo> getRandom(int amount) {
        Criteria criteria = getBuilder().build().getExecutableCriteria(sessionFactory.getCurrentSession());
        criteria.add(Restrictions.sqlRestriction("1=1 order by random()"));
        criteria.setMaxResults(amount);
        return criteria.list();
    }
}
