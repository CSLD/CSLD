package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
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

        TextField<String> name = new TextField<String>("name");
        name.setRequired(true);
        ComponentFeedbackMessageFilter nameFilter = new ComponentFeedbackMessageFilter(name);
        final FeedbackPanel nameFeedback = new FeedbackPanel("nameFeedback", nameFilter);
        nameFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(nameFeedback);
        name.add(new AjaxFeedbackUpdatingBehavior("blur", nameFeedback));
        createOrUpdateUser.add(name);


        TextField<String> nickname = new TextField<String>("nickname");
        ComponentFeedbackMessageFilter nicknameFilter = new ComponentFeedbackMessageFilter(nickname);
        final FeedbackPanel nicknameFeedback = new FeedbackPanel("nicknameFeedback", nicknameFilter);
        nicknameFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(nicknameFeedback);
        nickname.add(new AjaxFeedbackUpdatingBehavior("blur", nicknameFeedback));
        createOrUpdateUser.add(nickname);

        EmailTextField email = new EmailTextField("email");
        email.setRequired(true);
        email.add(new UniqueUserValidator(isEdit, personService));
        ComponentFeedbackMessageFilter emailFilter = new ComponentFeedbackMessageFilter(email);
        final FeedbackPanel emailFeedback = new FeedbackPanel("emailFeedback", emailFilter);
        emailFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(emailFeedback);
        email.add(new AjaxFeedbackUpdatingBehavior("blur", emailFeedback));
        createOrUpdateUser.add(email);

        TextArea<String> description = new TextArea<String>("description");
        ComponentFeedbackMessageFilter descriptionFilter = new ComponentFeedbackMessageFilter(description);
        final FeedbackPanel descriptionFeedback = new FeedbackPanel("descriptionFeedback", descriptionFilter);
        descriptionFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(descriptionFeedback);
        description.add(new AjaxFeedbackUpdatingBehavior("blur", descriptionFeedback));
        createOrUpdateUser.add(description);

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
