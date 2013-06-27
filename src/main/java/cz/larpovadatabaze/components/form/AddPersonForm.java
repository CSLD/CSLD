package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.PersonService;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 11.4.13
 * Time: 16:07
 */
public class AddPersonForm extends Form<Person> {
    @SpringBean
    PersonService personService;

    public AddPersonForm(String id) {
        super(id, new CompoundPropertyModel<Person>(new Person()));

        add(new FeedbackPanel("feedback"));

        add(new TextField<String>("name").setRequired(true));

        add(new TextField<String>("nickname"));

        add(new EmailTextField("email").setRequired(true));

        add(new DateTextField("birthDate", "dd.mm.yyyy").
                setRequired(true));

        add(new TextField<String>("city"));

        add(new Button("submit"));


    }

    protected void onSubmit() {
        Person person = getModelObject();
        validate();

        if(!hasError()){
            personService.insert(person);
        }
    }
}
