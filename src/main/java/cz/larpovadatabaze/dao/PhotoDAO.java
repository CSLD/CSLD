package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Photo;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class PhotoDAO extends GenericHibernateDAO<Photo, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Photo>(Photo.class);
    }
}
