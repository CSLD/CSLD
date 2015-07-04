package cz.larpovadatabaze.components.panel.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.components.common.CsldFeedbackMessageLabel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.lang.LocaleProvider;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.utils.Pwd;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

/**
 * Panel used for registering new user or adding new Author into the database.
 */
public abstract class CreateOrUpdateUserPanel extends Panel {

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

        EmailTextField email = new EmailTextField("person.email");
        email.setRequired(true);
        email.setLabel(Model.of("Email"));
        email.add(new UniqueUserValidator(isEdit, csldUserService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback", "form.loginMail"));

        PasswordTextField password = new PasswordTextField("password");
        password.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(password, createOrUpdateUser, "passwordFeedback", "form.description.password"));

        PasswordTextField passwordAgain =
            new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain"));
        passwordAgain.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(passwordAgain, createOrUpdateUser, "passwordAgainFeedback", "form.description.passwordAgain"));

        fileUpload = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this,"images"));
        createOrUpdateUser.add(addFeedbackPanel(fileUpload, createOrUpdateUser, "imageFeedback", "form.description.image"));


        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback", "form.description.wholeName"));


        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.nickname"), createOrUpdateUser, "nicknameFeedback", "form.description.nickname"));

        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.city"), createOrUpdateUser, "cityFeedback", "form.description.city"));

        DateTextField birthDate = new DateTextField("person.birthDate", "dd.mm.yyyy");
        createOrUpdateUser.add(addFeedbackPanel(birthDate, createOrUpdateUser, "birthDateFeedback", "form.description.dateOfBirth"));

        LocaleProvider locales = new CodeLocaleProvider();
        List<String> availableLocales = new ArrayList<String>();
        for(Locale locale: locales.availableLocale()){
            availableLocales.add(locale.getLanguage());
        }
        DropDownChoice<String> defaultLanguage = new DropDownChoice<String>("defaultLang", availableLocales);
        createOrUpdateUser.add(defaultLanguage);

        final ListMultipleChoice<Language> changeLocale =
                new ListMultipleChoice<Language>("userHasLanguages",
                        new CodeLocaleProvider().availableLanguages());
        createOrUpdateUser.add(addFeedbackPanel(changeLocale, createOrUpdateUser, "userHasLanguagesFeedback", "form.description.userHasLanguages"));
        
        ReCaptchaComponent reCaptcha = new ReCaptchaComponent("reCaptcha", new Model());
        createOrUpdateUser.add(addFeedbackPanel(reCaptcha, createOrUpdateUser, "reCaptchaFeedback", "form.description.reCaptcha"));

        TextArea description =new TextArea<String>("person.description");
        description.add(new CSLDTinyMceBehavior());
        createOrUpdateUser.add(description);


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