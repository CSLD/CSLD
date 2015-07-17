package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.util.ArrayList;
import java.util.List;

import static cz.larpovadatabaze.lang.AvailableLanguages.availableLocaleNames;

/**
 * Panel used for registering new user or adding new Author into the database.
 */
public abstract class CreateOrUpdateUserPanel extends AbstractCsldPanel<CsldUser> {

    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    FileService fileService;
    @SpringBean
    ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    private FileUploadField fileUpload;
    @SuppressWarnings("unused")
    private String passwordAgain;
    @SuppressWarnings("unused")
    private List<FileUpload> images = new ArrayList<>();

    private final String resourceBase;
    private final boolean isEdit;
    private final String oldPassword;

    public CreateOrUpdateUserPanel(String id, CsldUser user) {
        super(id);

        if(user == null) {
            user = CsldUser.getEmptyUser();
            this.resourceBase = "user.register";
            isEdit = false;
        }
        else {
            this.resourceBase = "user.edit";
            isEdit = true;
        }
        setDefaultModel(new CompoundPropertyModel<>(user));
        oldPassword = getModelObject().getPassword();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();


        final ValidatableForm<CsldUser> createOrUpdateUser =
                new ValidatableForm<>("addUser", getModel());
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        createOrUpdateUser.add(new Label("header", new StringResourceModel(resourceBase+".header", null)));
        createOrUpdateUser.add(new Label("subheader", new StringResourceModel(resourceBase+".subheader", null)));

        EmailTextField email = new EmailTextField("person.email");
        email.setRequired(true);
        email.setLabel(Model.of("Email"));
        email.add(new UniqueUserValidator(isEdit, csldUserService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback", "form.loginMail"));

        PasswordTextField password = new PasswordTextField("password");
        password.setRequired(!isEdit);
        createOrUpdateUser.add(addFeedbackPanel(password, createOrUpdateUser, "passwordFeedback", "form.description.password"));

        PasswordTextField passwordAgain =
            new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain"));
        passwordAgain.setRequired(!isEdit);
        createOrUpdateUser.add(addFeedbackPanel(passwordAgain, createOrUpdateUser, "passwordAgainFeedback", "form.description.passwordAgain"));

        fileUpload = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this,"images"));
        createOrUpdateUser.add(addFeedbackPanel(fileUpload, createOrUpdateUser, "imageFeedback", "form.description.image"));


        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback", "form.description.wholeName"));


        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.nickname"), createOrUpdateUser, "nicknameFeedback", "form.description.nickname"));

        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.city"), createOrUpdateUser, "cityFeedback", "form.description.city"));

        DateTextField birthDate = new DateTextField("person.birthDate", "dd.MM.yyyy");
        createOrUpdateUser.add(addFeedbackPanel(birthDate, createOrUpdateUser, "birthDateFeedback", "form.description.dateOfBirth"));

        List<String> availableLocales = new ArrayList<String>(availableLocaleNames());
        DropDownChoice<String> defaultLanguage = new DropDownChoice<String>("defaultLang", availableLocales);
        createOrUpdateUser.add(defaultLanguage);

        final ListMultipleChoice<String> changeLocale =
                new ListMultipleChoice<>("userHasLanguages",
                        availableLocaleNames());
        createOrUpdateUser.add(addFeedbackPanel(changeLocale, createOrUpdateUser, "userHasLanguagesFeedback", "form.description.userHasLanguages"));

        if (isEdit) {
            // No captcha
            WebMarkupContainer reCaptcha = new WebMarkupContainer("reCaptcha");
            reCaptcha.setVisible(false);
            createOrUpdateUser.add(reCaptcha);
        }
        else {
            // Captcha
            ReCaptchaComponent reCaptcha = new ReCaptchaComponent("reCaptcha", new Model());
            createOrUpdateUser.add(reCaptcha);
        }

        TextArea description =new TextArea<String>("person.description");
        description.add(new CSLDTinyMceBehavior());
        createOrUpdateUser.add(description);


        createOrUpdateUser.add(new AjaxButton("submit", new StringResourceModel(resourceBase+".submit", null)){
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

    private FormComponent addFeedbackPanel(FormComponent addFeedbackTo, Form addingFeedbackTo, String feedbackId, String defaultKey){
        addingFeedbackTo.add(new CsldFeedbackMessageLabel(feedbackId, addFeedbackTo, defaultKey));
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
        if(user.getPerson().getDescription() != null) {
            user.getPerson().setDescription(Jsoup.clean(user.getPerson().getDescription(), Whitelist.basic()));
        }
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCuttingSquareStrategy(CsldUserService.USER_IMAGE_SIZE, CsldUserService.USER_IMAGE_LEFTTOP_PERCENT)).path;
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

        // Process password
        if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(passwordAgain)) {
            // Keep password
            user.setPassword(oldPassword);
        }
        else {
            // Set new password
            user.setPassword(Pwd.generateStrongPasswordHash(user.getPassword(), user.getPerson().getEmail()));
        }

        if(csldUserService.saveOrUpdate(user)){
            return true;
        } else {
            error(getLocalizer().getString("user.cantAdd", this));
            return false;
        }
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}