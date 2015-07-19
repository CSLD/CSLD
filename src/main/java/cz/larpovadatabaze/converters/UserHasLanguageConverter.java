package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.dao.UserHasLanguagesDao;
import cz.larpovadatabaze.entities.UserHasLanguages;
import cz.larpovadatabaze.utils.UserUtils;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Locale;

/**
 * Converts between textual and database based representation of UserHasLanguage.
 */
public class UserHasLanguageConverter implements IConverter<UserHasLanguages> {
    private UserHasLanguagesDao userHasLanguages;

    public UserHasLanguageConverter(UserHasLanguagesDao userHasLanguages) {
        this.userHasLanguages = userHasLanguages;
    }

    @Override
    public UserHasLanguages convertToObject(String language, Locale locale) throws ConversionException {
        if(UserUtils.isSignedIn()) {
            List<UserHasLanguages> existingLanguages = userHasLanguages.findByCriteria(Restrictions.and(
                    Restrictions.eq("language", language),
                    Restrictions.eq("user", UserUtils.getLoggedUser())));
            if(!existingLanguages.isEmpty()){
                return existingLanguages.get(0);
            }
        }

        return createNewLanguage(language);
    }

    private UserHasLanguages createNewLanguage(String language) {
        UserHasLanguages newUserHasLanguage = new UserHasLanguages();
        newUserHasLanguage.setLanguage(language);
        newUserHasLanguage.setUser(UserUtils.getLoggedUser());
        return newUserHasLanguage;
    }

    @Override
    public String convertToString(UserHasLanguages userHasLanguages, Locale locale) {
        return userHasLanguages.getLanguage();
    }
}
