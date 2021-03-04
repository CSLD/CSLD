package cz.larpovadatabaze.users.services.masquerade;

import cz.larpovadatabaze.common.entities.EmailAuthentication;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.users.services.EmailAuthentications;

public class InMemoryEmailAuthenticationTokens extends InMemoryCrud<EmailAuthentication, Integer> implements EmailAuthentications {
    @Override
    public EmailAuthentication getByKey(String key) {
        for(EmailAuthentication auth: getAll()) {
            if (auth.getAuthToken().equals(key)) {
                return auth;
            }
        }

        return null;
    }
}
