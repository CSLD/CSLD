package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.EmailAuthentication;

/**
 *
 */
public interface EmailAuthenticationService extends GenericService<EmailAuthentication> {
    public void saveOrUpdate(EmailAuthentication emailAuthentication);

    public EmailAuthentication getByKey(String key);
}
