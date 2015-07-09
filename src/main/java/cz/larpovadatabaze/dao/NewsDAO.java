package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.News;

/**
 * Generic Dao for News.
 */
public class NewsDAO extends GenericHibernateDAO<News, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<News>(News.class);
    }
}
