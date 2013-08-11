package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.behavior.AjaxFeedbackUpdatingBehavior;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.utils.FileUtils;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

/**
 * Panel used for registering new user or adding new Author into the database.
 */
public abstract class CreateOrUpdateUserPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    ImageService imageService;
    @SpringBean
    PersonService personService;

    private FileUploadField fileUpload;
    private String passwordAgain;

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
        ComponentFeedbackMessageFilter nameFilter = new ComponentFeedbackMessageFilter(name);
        final FeedbackPanel nameFeedback = new FeedbackPanel("nameFeedback", nameFilter);
        nameFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(nameFeedback);
        name.add(new AjaxFeedbackUpdatingBehavior("blur", nameFeedback));
        createOrUpdateUser.add(name);

        TextField<String> nickname = new TextField<String>("person.nickname");
        ComponentFeedbackMessageFilter nicknameFilter = new ComponentFeedbackMessageFilter(nickname);
        final FeedbackPanel nicknameFeedback = new FeedbackPanel("nicknameFeedback", nicknameFilter);
        nicknameFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(nicknameFeedback);
        nickname.add(new AjaxFeedbackUpdatingBehavior("blur", nicknameFeedback));
        createOrUpdateUser.add(nickname);

        EmailTextField email = new EmailTextField("person.email");
        email.setRequired(true);
        email.add(new UniqueUserValidator(isEdit, personService));
        ComponentFeedbackMessageFilter emailFilter = new ComponentFeedbackMessageFilter(email);
        final FeedbackPanel emailFeedback = new FeedbackPanel("emailFeedback", emailFilter);
        emailFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(emailFeedback);
        email.add(new AjaxFeedbackUpdatingBehavior("blur", emailFeedback));
        createOrUpdateUser.add(email);


        DateTextField birthDate = new DateTextField("person.birthDate", "dd.mm.yyyy");
        birthDate.setRequired(true);
        ComponentFeedbackMessageFilter birthDateFilter = new ComponentFeedbackMessageFilter(birthDate);
        final FeedbackPanel birthDateFeedback = new FeedbackPanel("birthDateFeedback", birthDateFilter);
        birthDateFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(birthDateFeedback);
        birthDate.add(new AjaxFeedbackUpdatingBehavior("blur", birthDateFeedback));
        createOrUpdateUser.add(birthDate);

        TextField<String> city = new TextField<String>("person.city");
        ComponentFeedbackMessageFilter cityFilter = new ComponentFeedbackMessageFilter(city);
        final FeedbackPanel cityFeedback = new FeedbackPanel("cityFeedback", cityFilter);
        cityFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(cityFeedback);
        city.add(new AjaxFeedbackUpdatingBehavior("blur", cityFeedback));
        createOrUpdateUser.add(city);

        TextArea<String> description = new TextArea<String>("person.description");
        ComponentFeedbackMessageFilter descriptionFilter = new ComponentFeedbackMessageFilter(description);
        final FeedbackPanel descriptionFeedback = new FeedbackPanel("descriptionFeedback", descriptionFilter);
        descriptionFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(descriptionFeedback);
        description.add(new AjaxFeedbackUpdatingBehavior("blur", descriptionFeedback));
        createOrUpdateUser.add(description);

        fileUpload = new FileUploadField("image");
        ComponentFeedbackMessageFilter imageFilter = new ComponentFeedbackMessageFilter(fileUpload);
        final FeedbackPanel imageFeedback = new FeedbackPanel("imageFeedback", imageFilter);
        imageFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(imageFeedback);
        fileUpload.add(new AjaxFeedbackUpdatingBehavior("blur", imageFeedback));
        createOrUpdateUser.add(fileUpload);


        PasswordTextField password = new PasswordTextField("password");
        password.setRequired(true);
        ComponentFeedbackMessageFilter passwordFilter = new ComponentFeedbackMessageFilter(password);
        final FeedbackPanel passwordFeedback = new FeedbackPanel("passwordFeedback", passwordFilter);
        passwordFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(passwordFeedback);
        password.add(new AjaxFeedbackUpdatingBehavior("blur", passwordFeedback));
        createOrUpdateUser.add(password);


        PasswordTextField passwordAgain =
                new PasswordTextField("passwordAgain", new PropertyModel<String>(this, "passwordAgain"));
        passwordAgain.setRequired(true);
        ComponentFeedbackMessageFilter passwordAgainFilter = new ComponentFeedbackMessageFilter(passwordAgain);
        final FeedbackPanel passwordAgainFeedback = new FeedbackPanel("passwordAgainFeedback", passwordAgainFilter);
        passwordAgainFeedback.setOutputMarkupId(true);
        createOrUpdateUser.add(passwordAgainFeedback);
        passwordAgain.add(new AjaxFeedbackUpdatingBehavior("blur", passwordAgainFeedback));
        createOrUpdateUser.add(passwordAgain);

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
        });

        add(createOrUpdateUser);
    }

    private void saveOrUpdateUserAndImage(CsldUser user){
        final List<FileUpload> uploads = fileUpload.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                ServletContext context = ((Csld) Application.get()).getServletContext();
                String realPath = context.getRealPath(Csld.getBaseContext());
                File baseFile = new File(realPath);

                // Create a new file
                File newFile = new File(baseFile, upload.getClientFileName());

                // Check new file, delete if it already existed
                FileUtils.cleanFileIfExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);

                    Image image = new Image();
                    image.setPath(newFile.getAbsolutePath());
                    imageService.insert(image);
                    user.setImage(image);
                    user.setImageId(image.getId());
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
        personService.insert(user.getPerson());
        user.setPersonId(user.getPerson().getId());
        csldUserService.insert(user);
        csldUserService.flush();
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
