package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.PersonService;
import org.apache.wicket.util.convert.IConverter;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 15:23
 */
public class PersonConverter implements IConverter<Person> {
    private PersonService personService;

    public PersonConverter(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person convertToObject(String s, Locale locale) {
        return personService.getByEmail(s);
    }

    @Override
    public String convertToString(Person person, Locale locale) {
        return person.getEmail();
    }
}
