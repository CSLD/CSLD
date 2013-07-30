package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 *
 */
public class CsldUserConverter implements IConverter<CsldUser> {
    private CsldUserService csldUserService;

    public CsldUserConverter(CsldUserService csldUserService) {
        this.csldUserService = csldUserService;
    }

    @Override
    public CsldUser convertToObject(String value, Locale locale) {
        String[] personInfo = value.split(", ");

        CsldUser csldUser = new CsldUser();
        Person person = new Person();
        person.setEmail(personInfo[0]);
        csldUser.setPerson(person);

        List<CsldUser> correctUser = csldUserService.getUnique(csldUser);
        return correctUser.get(0);
    }

    @Override
    public String convertToString(CsldUser user, Locale locale) {
        return user.getAutoCompleteData();
    }
}
