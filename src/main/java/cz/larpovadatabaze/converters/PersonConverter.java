package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.PersonService;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts String to Person and Person to String
 * Unique for Person is email.
 */
public class PersonConverter implements IConverter<Person> {
    private PersonService personService;

    public PersonConverter(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person convertToObject(String autoCompletable, Locale locale) {
        try {
            List<Person> foundPersons = personService.getByAutoCompletable(autoCompletable);
            int amountOfGames = foundPersons.size();
            if(amountOfGames == 1) {
                return foundPersons.get(0);
            } else {
                return null;
            }
        } catch(WrongParameterException ex) {
            return null;
        }
    }

    @Override
    public String convertToString(Person person, Locale locale) {
        String stringRepresentation = person.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
