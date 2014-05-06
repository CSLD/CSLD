package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Image;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class ImageDAO extends GenericHibernateDAO<Image, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Image>(Image.class);
    }
}
