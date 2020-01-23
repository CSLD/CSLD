package cz.larpovadatabaze.users.converters;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts String to CsldUser and CsldUser to String.
 * Email of user is unique identifier.
 */
public class CsldUserConverter implements IConverter<CsldUser> {
    private TokenSearch tokenSearch;

    public CsldUserConverter(TokenSearch tokenSearch) {
        this.tokenSearch = tokenSearch;
    }

    @Override
    public CsldUser convertToObject(String autoCompletable, Locale locale) {
        List<CsldUser> foundUsers = tokenSearch.findUsers(autoCompletable);
        int amountOfUsers = foundUsers.size();
        if (amountOfUsers == 1) {
            return foundUsers.get(0);
        } else {
            CsldUser user = CsldUser.getEmptyUser();
            user.setId(-1);
            return user;
        }
    }

    @Override
    public String convertToString(CsldUser user, Locale locale) {
        String stringRepresentation = user.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
