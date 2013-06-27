package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.WicketApplication;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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
 * Time: 18:46
 */
public class AddImageForm extends Form<Void> {
    @SpringBean
    ImageService imageService;

    FileUploadField fileUploadField;

    public AddImageForm(String id) {
        super(id);

        add(new FeedbackPanel("feedback"));

        // set this form to multipart mode (allways needed for uploads!)
        setMultiPart(true);

        // Add one file input field
        add(fileUploadField = new FileUploadField("fileInput"));

        // Set maximum size to 100K for demo purposes
        setMaxSize(Bytes.kilobytes(1024));

        add(new Button("submit"));

    }

    protected void onSubmit() {
        validate();

        if(!hasError()){
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null)
            {
                for (FileUpload upload : uploads)
                {
                    ServletContext context = ((WicketApplication)Application.get()).getServletContext();
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
                        image.setPath(realPath);
                        imageService.insert(image);
                        info("saved file: " + upload.getClientFileName());

                    }
                    catch (Exception e)
                    {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
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
