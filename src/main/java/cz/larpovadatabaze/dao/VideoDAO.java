package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.Video;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class VideoDAO extends GenericHibernateDAO<Video, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<Video>(Video.class);
    }
}
