package cz.larpovadatabaze.dao;

import cz.larpovadatabaze.api.GenericHibernateDAO;
import cz.larpovadatabaze.dao.builder.GenericBuilder;
import cz.larpovadatabaze.dao.builder.IBuilder;
import cz.larpovadatabaze.entities.EmailAuthentication;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class EmailAuthenticationDAO extends GenericHibernateDAO<EmailAuthentication, Integer>{
    @Override
    public IBuilder getBuilder() {
        return new GenericBuilder<EmailAuthentication>(EmailAuthentication.class);
    }


}
