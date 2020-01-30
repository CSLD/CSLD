package cz.larpovadatabaze.users.components.panel;

import cz.larpovadatabaze.common.components.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.common.components.ValidatableForm;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.users.services.AppUsers;
import cz.larpovadatabaze.users.services.CsldUsers;
import cz.larpovadatabaze.users.validator.UniqueUserValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel used for registering new author or adding new Author into the database.
 */
public abstract class CreateOrUpdateAuthorPanel extends Panel {
    @SpringBean
    CsldUsers csldUsers;
    @SpringBean
    AppUsers appUsers;

    public CreateOrUpdateAuthorPanel(String id, CsldUser author) {
        super(id);

        boolean isEdit = true;
        if (author == null) {
            isEdit = false;
            author = CsldUser.getEmptyUser();
        }

        final ValidatableForm<CsldUser> createOrUpdateUser = new ValidatableForm<CsldUser>("addUser", new CompoundPropertyModel<CsldUser>(author));
        createOrUpdateUser.setMultiPart(false);
        createOrUpdateUser.setOutputMarkupId(true);

        EmailTextField email = new EmailTextField("person.email");
        email.add(new UniqueUserValidator(false, csldUsers, appUsers));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback", "form.loginMail"));

        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback", "form.description.wholeName"));


        TextField<String> nickname = new TextField<String>("person.nickname");
        createOrUpdateUser.add(addFeedbackPanel(nickname, createOrUpdateUser, "nicknameFeedback", "form.description.nickname"));

        TextArea<String> description = new TextArea<String>("person.description");
        createOrUpdateUser.add(description);

        createOrUpdateUser.add(new AjaxSubmitLink("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                if(createOrUpdateUser.isValid()){
                    CsldUser author = createOrUpdateUser.getModelObject();
                    if (csldUsers.saveOrUpdateNewAuthor(author)) {
                        onCsldAction(target, author);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                // Refresh form
                target.add(CreateOrUpdateAuthorPanel.this);
            }
        });

        add(createOrUpdateUser);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String feedbackId, String defaultKey){
        addingFeedbackTo.add(new CsldFeedbackMessageLabel(feedbackId, addFeedbackTo, defaultKey));
        return addFeedbackTo;
    }

    protected void onCsldAction(AjaxRequestTarget target, Object object){}
}
