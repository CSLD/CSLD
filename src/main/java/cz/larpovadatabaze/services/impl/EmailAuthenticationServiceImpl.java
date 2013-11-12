package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.EmailAuthenticationDAO;
import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.services.EmailAuthenticationService;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class EmailAuthenticationServiceImpl implements EmailAuthenticationService {
    @Autowired
    EmailAuthenticationDAO emailAuthenticationDAO;

    @Override
    public List<EmailAuthentication> getAll() {
        return emailAuthenticationDAO.findAll();
    }

    @Override
    public List<EmailAuthentication> getUnique(EmailAuthentication example) {
        return emailAuthenticationDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(EmailAuthentication toRemove) {
        emailAuthenticationDAO.makeTransient(toRemove);
    }

    @Override
    public List<EmailAuthentication> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public void saveOrUpdate(EmailAuthentication emailAuthentication) {
        emailAuthenticationDAO.saveOrUpdate(emailAuthentication);
    }

    @Override
    public EmailAuthentication getByKey(String key) {
        return emailAuthenticationDAO.findSingleByCriteria(Restrictions.eq("authToken", key));
    }
}
