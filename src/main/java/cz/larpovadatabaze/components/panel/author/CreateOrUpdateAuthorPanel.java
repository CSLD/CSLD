package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel used for registering new author or adding new Author into the database.
 */
public class CreateOrUpdateAuthorPanel extends Panel {
    @SpringBean
    PersonService personService;
    @SpringBean
    CsldUserService csldUserService;

    public CreateOrUpdateAuthorPanel(String id, Person author) {
        super(id);

        boolean isEdit = true;
        if(author == null) {
            isEdit = false;
            author = Person.getEmptyPerson();
        }

        Form<Person> createOrUpdateUser = new Form<Person>("addUser", new CompoundPropertyModel<Person>(author)){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();

                if(!hasError()) {
                    Person author = getModelObject();
                    saveOrUpdateUser(author);
                }
            }
        };
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        createOrUpdateUser.add(feedback);

        createOrUpdateUser.add(new TextField<String>("name").setRequired(true));
        createOrUpdateUser.add(new TextField<String>("nickname"));
        createOrUpdateUser.add(new EmailTextField("email").
                setRequired(true).
                add(new UniqueUserValidator(isEdit, personService)));
        createOrUpdateUser.add(new TextArea<String>("description"));

        createOrUpdateUser.add(new Button("submit"));

        add(createOrUpdateUser);
    }

    private void saveOrUpdateUser(Person author){
        personService.insert(author);
        CsldUser authorUser = CsldUser.getEmptyUser();
        authorUser.setPerson(author);
        authorUser.setPersonId(author.getId());
        authorUser.setPassword(Pwd.getMD5("defaultPWD")); // TODO get Random String for password.
        csldUserService.insert(authorUser);
    }
}
