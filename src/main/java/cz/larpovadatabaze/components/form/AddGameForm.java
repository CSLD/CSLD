package cz.larpovadatabaze.components.form;

import cz.larpovadatabaze.WicketApplication;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.GameService;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.4.13
 * Time: 16:18
 */
public class AddGameForm extends Form<Game> {
    @SpringBean
    GameService gameService;

    @SpringBean
    ImageService imageService;

    private FileUploadField fileUploadField;
    private String videoPath;
    private List<FileUpload> uploads;

    public AddGameForm(String id, Game game) {
        super(id, new CompoundPropertyModel<Game>(game));

        add(new FeedbackPanel("feedback"));

        add(new TextField<String>("name").setRequired(true));

        add(new TextField<String>("description").setRequired(true));

        add(new TextField<Integer>("year"));

        add(new TextField<String>("web"));

        add(new TextField<Integer>("hours"));

        add(new TextField<Integer>("days"));

        add(new TextField<Integer>("players"));

        add(new TextField<Integer>("menRole"));

        add(new TextField<Integer>("womenRole"));

        add(new TextField<Integer>("bothRole"));

        add(new TextField<String>("videoPath",
                new PropertyModel<String>(this, "videoPath")));

        setMultiPart(true);

        // Add one file input field
        add(fileUploadField = new FileUploadField("image",
                new PropertyModel<List<FileUpload>>(this, "uploads")));

        // Set maximum size to 100K for demo purposes
        setMaxSize(Bytes.kilobytes(1024));

        add(new Button("submit"));

        setOutputMarkupId(true);
    }

    protected void onSubmit() {
        Game game = getModelObject();
        game.setAdded(new Timestamp(new Date().getTime()));
        validate();

        if(!hasError()){
            final List<FileUpload> uploads = fileUploadField.getFileUploads();
            if (uploads != null)
            {
                for (FileUpload upload : uploads)
                {
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
                        image.setPath(realPath);
                        imageService.insert(image);

                        game.setImage(image);
                        gameService.addGame(game);
                        gameService.flush();
                    }
                    catch (Exception e)
                    {
                        throw new IllegalStateException("Unable to write file", e);
                    }
                }
            } else {
                game.setAdded(new Timestamp(new Date().getTime()));
                gameService.addGame(game);
                gameService.flush();
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
