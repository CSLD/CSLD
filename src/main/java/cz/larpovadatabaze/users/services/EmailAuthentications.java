package cz.larpovadatabaze.users.services;

import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.services.CRUDService;

/**
 *
 */
public interface EmailAuthentications extends CRUDService<EmailAuthentication, Integer> {
    EmailAuthentication getByKey(String key);
}
