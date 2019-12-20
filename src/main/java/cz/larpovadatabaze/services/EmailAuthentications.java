package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.EmailAuthentication;

/**
 *
 */
public interface EmailAuthentications extends CRUDService<EmailAuthentication, Integer> {
    EmailAuthentication getByKey(String key);
}
