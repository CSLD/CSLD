package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.CsldUsers;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts String to CsldUser and CsldUser to String.
 * Email of user is unique identifier.
 */
public class CsldUserConverter implements IConverter<CsldUser> {
    private CsldUsers csldUsers;

    public CsldUserConverter(CsldUsers csldUsers) {
        this.csldUsers = csldUsers;
    }

    @Override
    public CsldUser convertToObject(String autoCompletable, Locale locale) {
        try {
            List<CsldUser> foundUsers = csldUsers.getByAutoCompletable(autoCompletable);
            int amountOfUsers = foundUsers.size();
            if (amountOfUsers == 1) {
                return foundUsers.get(0);
            } else {
                CsldUser user = CsldUser.getEmptyUser();
                user.setId(-1);
                return user;
            }
        } catch(WrongParameterException ex) {
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
