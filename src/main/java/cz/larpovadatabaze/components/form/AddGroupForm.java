package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.GroupService;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.lang.Bytes;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 18:12
 */
public class AddGroupForm extends Form<CsldGroup> {
    @SpringBean
    GroupService groupService;

    @SpringBean
    ImageService imageService;

    private FileUploadField fileUploadField;
    private List<FileUpload> uploads;

    public AddGroupForm(String id) {
        super(id, new CompoundPropertyModel<CsldGroup>(new CsldGroup()));

        add(new FeedbackPanel("feedback"));

        add(new TextField<String>("name").setRequired(true));

        setMultiPart(true);

        // Add one file input field
        add(fileUploadField = new FileUploadField("image",
                new PropertyModel<List<FileUpload>>(this, "uploads")));

        // Set maximum size to 1024K for demo purposes
        setMaxSize(Bytes.kilobytes(1024));

        add(new Button("submit"));
    }

    protected void onSubmit() {
        CsldGroup group = getModelObject();
        validate();

        if(!hasError()){
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null)
            {
                for (FileUpload upload : uploads)
                {
                    ServletContext context = ((Csld) Application.get()).getServletContext();
                    String baseContext = "/files/upload/";
                    String realPath = context.getRealPath(baseContext);
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
                        image.setPath(baseContext + upload.getClientFileName());
                        imageService.insert(image);

                        group.setImage(image);
                        group.setImageId(image.getId());
                        groupService.insert(group);
                    }
                    catch (Exception e)
                    {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
            } else {
                groupService.insert(group);
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
