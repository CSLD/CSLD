package org.apache.wicket.extensions.ajax.markup.html.autocomplete;

import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.PersonService;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.5.13
 * Time: 16:49
 */
public class RepeatableForm extends Form {
    @SpringBean
    private PersonService personService;

    public RepeatableForm(String id) {
        super(id);

        Person person = new Person();
        GenericFactory<Person> factory = new GenericFactory<Person>(Person.class);
        GenericValidator<Person> validator = new GenericValidator<Person>(personService);

        RepeatableInputPanel repeatable =
                new RepeatableInputPanel<Person>("repeatablePanel", factory,
                        validator, personService);
        repeatable.setOutputMarkupId(true);
        add(repeatable);

        add(new Button("submit"));
    }

    @Override
    protected void onSubmit() {
        System.out.println("Submitting");
    }
}
