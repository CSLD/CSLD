package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.services.EmailAuthentications;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tokens created for the user when requesting a new password.
 */
@Repository
@Transactional
public class SqlEmailAuthenticationTokens extends CRUD<EmailAuthentication, Integer> implements EmailAuthentications {
    @Autowired
    public SqlEmailAuthenticationTokens(SessionFactory sessionFactory) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(EmailAuthentication.class)));
    }

    @Override
    public EmailAuthentication getByKey(String key) {
        return crudRepository.findSingleByCriteria(Restrictions.eq("authToken", key));
    }
}
