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
import cz.larpovadatabaze.utils.Pwd;
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
import java.util.ArrayList;
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
        createOrUpdateUser.add(addFeedbackPanel(name, createOrUpdateUser, "nameFeedback"));
        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.nickname"), createOrUpdateUser, "nicknameFeedback"));

        EmailTextField email = new EmailTextField("person.email");
        email.setRequired(true);
        email.add(new UniqueUserValidator(isEdit, personService));
        createOrUpdateUser.add(addFeedbackPanel(email, createOrUpdateUser, "emailFeedback"));

        DateTextField birthDate = new DateTextField("person.birthDate", "dd.mm.yyyy");
        birthDate.setRequired(true);
        createOrUpdateUser.add(addFeedbackPanel(birthDate, createOrUpdateUser, "birthDateFeedback"));

        createOrUpdateUser.add(addFeedbackPanel(new TextField<String>("person.city"), createOrUpdateUser,"cityFeedback"));
        createOrUpdateUser.add(addFeedbackPanel(new TextArea<String>("person.description"), createOrUpdateUser,"descriptionFeedback"));

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

    private boolean saveOrUpdateUserAndImage(CsldUser user){
        final List<FileUpload> uploads = fileUpload.getFileUploads();
        if (uploads != null) {
            for (FileUpload upload : uploads) {
                ServletContext context = ((Csld) Application.get()).getServletContext();
                String realPath = context.getRealPath(Csld.getBaseContext());
                File baseFile = new File(realPath);
                String fileName = Pwd.getMD5(upload.getClientFileName() + user.getPerson().getName()) + "." + FileUtils.getFileType(upload.getClientFileName());
                // Create a new file
                File newFile = new File(baseFile, fileName);

                // Check new file, delete if it already existed
                FileUtils.cleanFileIfExists(newFile);
                try
                {
                    baseFile.mkdirs();
                    // Save to new file
                    if(!newFile.createNewFile()) {
                        throw new IllegalStateException("Unable to write file " + newFile.getAbsolutePath());
                    }
                    upload.writeTo(newFile);

                    Image image = new Image();
                    image.setPath(Csld.getBaseContext() + fileName);
                    imageService.insert(image);
                    user.setImage(image);
                    user.setImageId(image.getId());
                    if(saveOrUpdateUser(user)){
                        return true;
                    } else {
                        imageService.remove(image);
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
        if(personService.saveOrUpdate(user.getPerson())){
            error("Uživatele se nepovedlo uložit.");
            return false;
        }
        user.setIsAuthor(false);
        user.setPersonId(user.getPerson().getId());
        user.setPassword(Pwd.getMD5(user.getPassword()));
        if(csldUserService.saveOrUpdate(user)){
            return true;
        } else {
            personService.remove(user.getPerson());
            error("Uživatele se nepovedlo uložit.");
            return false;
        }
    }

    protected void onCsldAction(AjaxRequestTarget target, Form<?> form){}
}
