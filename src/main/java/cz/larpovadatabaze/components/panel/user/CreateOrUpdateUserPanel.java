package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.behavior.ErrorClassAppender;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.utils.FileUtils;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel used for registering new user or adding new Author into the database.
 */
public abstract class CreateOrUpdateUserPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    private FileUploadField fileUpload;
    @SuppressWarnings("unused")
    private String passwordAgain;
    @SuppressWarnings("unused")
    private List<FileUpload> images = new ArrayList<FileUpload>();

    public CreateOrUpdateUserPanel(String id, CsldUser user) {
        super(id);

        boolean isEdit = true;
        if(user == null) {
            isEdit = false;
            user = CsldUser.getEmptyUser();
        }

        final ValidatableForm<CsldUser> createOrUpdateUser =
                new ValidatableForm<CsldUser>("addUser", new CompoundPropertyModel<CsldUser>(user));
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        name.setLabel(Model.of("Jméno"));
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback"));
        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.nickname"), createOrUpdateUser, "nicknameFeedback"));

        EmailTextField email = new EmailTextField("person.email");
        email.setRequired(true);
        email.setLabel(Model.of("Email"));
        email.add(new UniqueUserValidator(isEdit, csldUserService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback"));

        DateTextField birthDate = new DateTextField("person.birthDate", "dd.mm.yyyy");
        birthDate.setLabel(Model.of("Datum narození"));
        createOrUpdateUser.add(addFeedbackPanel(birthDate, createOrUpdateUser, "birthDateFeedback"));

        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.city"), createOrUpdateUser,"cityFeedback"));
        TextArea description =new TextArea<String>("person.description");
        description.add(new CSLDTinyMceBehavior());
        createOrUpdateUser.add(addFeedbackPanel(description, createOrUpdateUser, "descriptionFeedback"));

        fileUpload = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this,"images"));
        createOrUpdateUser.add(addFeedbackPanel(fileUpload, createOrUpdateUser,"imageFeedback"));

        PasswordTextField password = new PasswordTextField("password");
        password.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(password, createOrUpdateUser, "passwordFeedback"));

        PasswordTextField passwordAgain =
                new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain"));
        passwordAgain.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(passwordAgain, createOrUpdateUser, "passwordAgainFeedback"));

        createOrUpdateUser.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createOrUpdateUser.isValid()){
                    CsldUser user = createOrUpdateUser.getModelObject();
                    if(saveOrUpdateUserAndImage(user)){
                        onCsldAction(target, form);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                if(!createOrUpdateUser.isValid()){
                    target.add(getParent());
                }
            }
        }.add(new TinyMceAjaxSubmitModifier()));
        createOrUpdateUser.add(new EqualPasswordInputValidator(password, passwordAgain));

        add(createOrUpdateUser);
    }

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String nameOfFeedbackPanel){
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(addFeedbackTo);
        final FeedbackPanel feedbackPanel = new FeedbackPanel(nameOfFeedbackPanel, filter);
        feedbackPanel.setOutputMarkupId(true);
        addingFeedbackTo.add(feedbackPanel);
        addFeedbackTo.add(new AjaxFeedbackUpdatingBehavior("blur", feedbackPanel));
        addFeedbackTo.add(new ErrorClassAppender());
        return addFeedbackTo;
    }

    private boolean saveOrUpdateUserAndImage(CsldUser user){
        final List<FileUpload> uploads = fileUpload.getFileUploads();
        if(user.getAmountOfComments() == null){
            user.setAmountOfComments(0);
        }
        if(user.getAmountOfCreated() == null) {
            user.setAmountOfCreated(0);
        }
        if(user.getAmountOfPlayed() == null) {
            user.setAmountOfPlayed(0);
        }
        if(user.getImage() == null){
            user.setImage(Image.getDefaultUser());
        }
        if(user.getPerson().getDescription() != null) {
            user.getPerson().setDescription(Jsoup.clean(user.getPerson().getDescription(), Whitelist.basic()));
        }
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = FileUtils.saveImageFileAndReturnPath(upload, user.getPerson().getName(), 120, 120);
                try
                {
                    Image image = new Image();
                    image.setPath(filePath);
                    user.setImage(image);
                    if(saveOrUpdateUser(user)){
                        return true;
                    } else {
                        return false;
                    }
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            return saveOrUpdateUser(user);
        }
        return false;
    }

    private boolean saveOrUpdateUser(CsldUser user){
        user.setIsAuthor(false);
        user.setPassword(Pwd.generateStrongPasswordHash(user.getPassword(), user.getPerson().getEmail()));
        if(csldUserService.saveOrUpdate(user)){
            return true;
        } else {
            error(getLocalizer().getString("user.cantAdd", this));
            return false;
        }
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}