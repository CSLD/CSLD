package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.UserHasLanguages;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation of UserHasLanguage entity.
 */
@Repository
public class UserHasLanguagesDao extends GenericHibernateDAO<UserHasLanguages, Integer> {
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<>(UserHasLanguages.class);
    }
}
