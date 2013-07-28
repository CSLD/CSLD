package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.WicketApplication;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Person;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.CsldUserService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.services.PersonService;
import cz.larpovadatabaze.validator.UniqueUserValidator;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.time.Duration;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 26.4.13
 * Time: 9:32
 */
public class RegisterForm extends Form<CsldUser> {
    @SpringBean
    CsldUserService csldUserService;
    @SpringBean
    ImageService imageService;
    @SpringBean
    PersonService personService;

    private FileUploadField fileUpload;
    private List<FileUpload> uploads;
    private String passwordAgain;

    public RegisterForm(String id, CsldUser csldUser) {
        super(id, new CompoundPropertyModel<CsldUser>(csldUser));
        boolean isEdit = true;
        if(csldUser.getPerson() == null){
            isEdit = false;
            getModelObject().setPerson(new Person());
        }

        setMultiPart(true);
        setOutputMarkupId(true);

        add(fileUpload = new FileUploadField("image",
                new PropertyModel<List<FileUpload>>(this, "uploads")));

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new TextField<String>("person.name").setRequired(true));

        add(new TextField<String>("person.nickname"));

        add(new EmailTextField("person.email").
                setRequired(true).
                add(new UniqueUserValidator(isEdit, personService)));

        add(new DateTextField("person.birthDate", "dd.mm.yyyy").
                setRequired(true));

        add(new TextField<String>("person.city"));

        add(new TextArea<String>("person.description"));

        add(new PasswordTextField("password"));

        add(new PasswordTextField("passwordAgain",
                new PropertyModel<String>(this, "passwordAgain")));

        add(new Button("submit"));

        AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
    }

    protected void onSubmit(){
        CsldUser user = getModelObject();
        user.setRole(CsldRoles.USER.getRole());
        validate();
        if(!hasError()) {
            final List<FileUpload> uploads = fileUpload.getFileUploads();
            if (uploads != null) {
                for (FileUpload upload : uploads) {
                    ServletContext context = ((WicketApplication) Application.get()).getServletContext();
                    String realPath = context.getRealPath("/files/upload/");
                    File baseFile = new File(realPath);

                    // Create a new file
                    File newFile = new File(baseFile, upload.getClientFileName());

                    // Check new file, delete if it already existed
                    checkFileExists(newFile);
                    try
                    {
                        // Save to new file
                        newFile.createNewFile();
                        upload.writeTo(newFile);

                        Image image = new Image();
                        image.setPath(newFile.getAbsolutePath());
                        imageService.insert(image);
                        info("saved file: " + upload.getClientFileName());
                        user.setImage(image);
                        user.setImageId(image.getId());
                        personService.insert(user.getPerson());
                        user.setPersonId(user.getPerson().getId());
                        csldUserService.insert(user);
                        csldUserService.flush();
                    }
                    catch (Exception e)
                    {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
            } else {
                personService.insert(user.getPerson());
                user.setPersonId(user.getPerson().getId());
                csldUserService.insert(user);
                csldUserService.flush();
            }
        }
    }

    private void checkFileExists(File newFile)
    {
        if (newFile.exists())
        {
            // Try to delete the file
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }
}
