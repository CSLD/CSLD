package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.EmailAuthentication;

/**
 *
 */
public interface EmailAuthenticationService extends GenericService<EmailAuthentication> {
    void saveOrUpdate(EmailAuthentication emailAuthentication);

    EmailAuthentication getByKey(String key);
}
