package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.behavior.CSLDTinyMceBehavior;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Language;
import cz.larpovadatabaze.lang.CodeLocaleProvider;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.FileService;
import cz.larpovadatabaze.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.Pwd;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;

import java.util.List;

/**
 * This panel allows editing of user.
 */
public class UpdateUserPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    ImageService imageService;
    @SpringBean
    FileService fileService;
    @SpringBean
    ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    private FileUploadField fileUpload;
    @SuppressWarnings("unused")
    private String passwordAgain;
    @SuppressWarnings("unused")
    private List<FileUpload> images;
    private String password;

    public UpdateUserPanel(String id, CsldUser user) {
        super(id);

        boolean isEdit = true;
        if(user == null) {
            isEdit = false;
            user = CsldUser.getEmptyUser();
        }
        this.password = user.getPassword();

        final ValidatableForm<CsldUser> createOrUpdateUser =
                new ValidatableForm<CsldUser>("addUser", new CompoundPropertyModel<CsldUser>(user));
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        TextField<String> name = new TextField<String>("person.name");
        name.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback"));
        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.nickname"), createOrUpdateUser, "nicknameFeedback"));

        DateTextField birthDate = new DateTextField("person.birthDate", "dd.MM.yyyy");
        createOrUpdateUser.add(addFeedbackPanel(birthDate, createOrUpdateUser, "birthDateFeedback"));

        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.city"), createOrUpdateUser,"cityFeedback"));

        TextArea description = new TextArea<String>("person.description");
        description.add(new CSLDTinyMceBehavior());
        createOrUpdateUser.add(addFeedbackPanel(description, createOrUpdateUser,"descriptionFeedback"));

        fileUpload = new FileUploadField("image", new PropertyModel<List<FileUpload>>(this, "images"));
        createOrUpdateUser.add(addFeedbackPanel(fileUpload, createOrUpdateUser,"imageFeedback"));

        PasswordTextField password = new PasswordTextField("password");
        password.setRequired(false);
        createOrUpdateUser.add(addFeedbackPanel(password, createOrUpdateUser, "passwordFeedback"));

        PasswordTextField passwordAgain =
                new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain"));
        passwordAgain.setRequired(false);
        createOrUpdateUser.add(addFeedbackPanel(passwordAgain, createOrUpdateUser, "passwordAgainFeedback"));

        final ListMultipleChoice<Language> changeLocale =
                new ListMultipleChoice<Language>("userHasLanguages",
                        new CodeLocaleProvider().availableLanguages());
        createOrUpdateUser.add(addFeedbackPanel(changeLocale, createOrUpdateUser, "userHasLanguagesFeedback"));

        DropDownChoice<Language> defaultLanguage =
                new DropDownChoice<Language>("defaultLang", new CodeLocaleProvider().availableLanguages());
        createOrUpdateUser.add(defaultLanguage);

        createOrUpdateUser.add(new AjaxButton("submit"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);

                if(createOrUpdateUser.isValid()){
                    CsldUser user = createOrUpdateUser.getModelObject();
                    saveOrUpdateUserAndImage(user);
                    onCsldAction(target, form);
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
        return addFeedbackTo;
    }

    private void saveOrUpdateUserAndImage(CsldUser user){
        if((user.getPassword() == null || user.getPassword().equals("")) &&
                (passwordAgain == null || passwordAgain.equals(""))){
            user.setPassword(this.password);
        } else {
            user.setPassword(Pwd.generateStrongPasswordHash(user.getPassword(), user.getPerson().getEmail()));
        }

        if(user.getPerson().getDescription() != null) {
            user.getPerson().setDescription(Jsoup.clean(user.getPerson().getDescription(), Whitelist.basic()));
        }
        final List<FileUpload> uploads = fileUpload.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                String filePath = fileService.saveImageFileAndReturnPath(upload, imageResizingStrategyFactoryService.getCuttingSquareStrategy(CsldUserService.USER_IMAGE_SIZE, CsldUserService.USER_IMAGE_LEFTTOP_PERCENT)).path;
                try
                {
                    Image image = new Image();
                    image.setPath(filePath);
                    imageService.insert(image);
                    user.setImage(image);
                    saveOrUpdateUser(user);
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file", e);
                }
            }
        } else {
            saveOrUpdateUser(user);
        }
    }

    private void saveOrUpdateUser(CsldUser user){
        csldUserService.saveOrUpdate(user);
        csldUserService.flush();
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
