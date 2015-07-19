package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.UserHasLanguages;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation of UserHasanguage entity.
 */
@Repository
public class UserHasLanguagesDao extends GenericHibernateDAO<UserHasLanguages, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<>(UserHasLanguages.class);
    }
}
