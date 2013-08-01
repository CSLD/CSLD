package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.utils.FileUtils;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

/**
 * Panel used for registering new user or adding new Author into the database.
 */
public class CreateOrUpdateUserPanel extends Panel {
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

        Form<CsldUser> createOrUpdateUser = new Form<CsldUser>("addUser", new CompoundPropertyModel<CsldUser>(user)){
            @Override
            protected void onSubmit() {
                super.onSubmit();
                validate();

                if(!hasError()) {
                    CsldUser user = getModelObject();
                    saveOrUpdateUserSubmit(user);
                }
            }
        };
        createOrUpdateUser.setMultiPart(true);
        createOrUpdateUser.setOutputMarkupId(true);

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        createOrUpdateUser.add(feedback);

        createOrUpdateUser.add(new TextField<String>("person.name").setRequired(true));
        createOrUpdateUser.add(new TextField<String>("person.nickname"));
        createOrUpdateUser.add(new EmailTextField("person.email").
                setRequired(true).
                add(new UniqueUserValidator(isEdit, personService)));
        createOrUpdateUser.add(new DateTextField("person.birthDate", "dd.mm.yyyy").
                setRequired(true));
        createOrUpdateUser.add(new TextField<String>("person.city"));
        createOrUpdateUser.add(new TextArea<String>("person.description"));

        createOrUpdateUser.add(fileUpload = new FileUploadField("image"));
        createOrUpdateUser.add(new PasswordTextField("password"));
        createOrUpdateUser.add(new PasswordTextField("passwordAgain",
                new PropertyModel<String>(this, "passwordAgain")));

        createOrUpdateUser.add(new Button("submit"));

        AjaxFormValidatingBehavior.addToAllFormComponents(createOrUpdateUser, "keydown", Duration.ONE_SECOND);

        add(createOrUpdateUser);
    }

    private void saveOrUpdateUserSubmit(CsldUser user){
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
}
