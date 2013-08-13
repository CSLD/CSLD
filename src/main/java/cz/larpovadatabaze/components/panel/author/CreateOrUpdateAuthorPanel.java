package cz.larpovadatabaze.components.panel.author;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.components.page.about.AboutDatabase;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.utils.RandomString;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel used for registering new author or adding new Author into the database.
 */
public abstract class CreateOrUpdateAuthorPanel extends Panel {
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

        final ValidatableForm<Person> createOrUpdateUser = new ValidatableForm<Person>("addUser", new CompoundPropertyModel<Person>(author));
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        TextField<String> name = new TextField<String>("name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback"));


        TextField<String> nickname = new TextField<String>("nickname");
        createOrUpdateUser.add(addFeedbackPanel(nickname, createOrUpdateUser, "nicknameFeedback"));

        EmailTextField email = new EmailTextField("email");
        email.setRequired(true);
        email.add(new UniqueUserValidator(isEdit, personService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback"));

        TextArea<String> description = new TextArea<String>("description");
        createOrUpdateUser.add(addFeedbackPanel(description, createOrUpdateUser, "descriptionFeedback"));

        createOrUpdateUser.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if(createOrUpdateUser.isValid()){
                    Person author = createOrUpdateUser.getModelObject();
                    saveOrUpdateUser(author);
                    onCsldAction(target, form);
                }
            }
        });

        add(createOrUpdateUser);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String nameOfFeedbackPanel){
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(addFeedbackTo);
        final FeedbackPanel feedbackPanel = new FeedbackPanel(nameOfFeedbackPanel, filter);
        feedbackPanel.setOutputMarkupId(true);
        addingFeedbackTo.add(feedbackPanel);
        addFeedbackTo.add(new AjaxFeedbackUpdatingBehavior("blur", feedbackPanel));
        return addFeedbackTo;
    }

    private void saveOrUpdateUser(Person author){
        personService.saveOrUpdate(author);
        CsldUser authorUser = CsldUser.getEmptyUser();
        authorUser.setPerson(author);
        authorUser.setPersonId(author.getId());
        authorUser.setIsAuthor(true);
        authorUser.setPassword(Pwd.getMD5(new RandomString(12).nextString()));
        csldUserService.saveOrUpdate(authorUser);
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
