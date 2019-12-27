package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.entities.EmailAuthentication;
import cz.larpovadatabaze.services.EmailAuthentications;

public class InMemoryEmailAuthenticationTokens extends InMemoryCrud<EmailAuthentication, Integer> implements EmailAuthentications {
    @Override
    public EmailAuthentication getByKey(String key) {
        return null;
    }
}
