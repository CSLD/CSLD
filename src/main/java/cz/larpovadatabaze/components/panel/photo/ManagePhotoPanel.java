package cz.larpovadatabaze.components.panel.photo;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.PhotoService;
import cz.larpovadatabaze.utils.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It allows user to basically manage photos.
 */
public class ManagePhotoPanel extends Panel {
    @SpringBean
    GameService gameService;
    @SpringBean
    PhotoService photoService;

    private FileUploadField photoOne;
    private FileUploadField photoTwo;
    private FileUploadField photoThree;
    private FileUploadField photoFour;
    private FileUploadField photoFive;

    private List<Photo> photosToManage;
    private Form<?> managePhotos;

    private boolean show;
    private final IModel<Game> model;

    public ManagePhotoPanel(String id, List<Photo> pPhotosToManage, boolean show, IModel<Game> model) {
        super(id);
        this.show = show;
        this.model = model;
        this.photosToManage = pPhotosToManage;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = model.getObject();

        add(new FeedbackPanel("uploadedPhotosFeedback").setOutputMarkupId(true));

        final ShowPhotoPanel showPhotos = new ShowPhotoPanel("uploadedPhotos", photosToManage);
        add(showPhotos);

        managePhotos = new Form<Void>("uploadPhotos"){};
        photoOne = addPhoto("photoOne");
        photoTwo = addPhoto("photoTwo");
        photoThree = addPhoto("photoThree");
        photoFour = addPhoto("photoFour");
        photoFive = addPhoto("photoFive");

        managePhotos.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                boolean uploadedPhoto = photoOne.getFileUpload() != null &&
                        photoTwo.getFileUpload() != null &&
                        photoThree.getFileUpload() != null &&
                        photoFour.getFileUpload() != null &&
                        photoFive.getFileUpload() != null;

                Game game = model.getObject();

                List<Photo> photos = new ArrayList<Photo>(game.getPhotos());
                photosToManage.removeAll(photosToManage);
                game.getPhotos().removeAll(photos);
                for(Photo photo: photos){
                    photoService.remove(photo);
                }
                handlePhotoFile(photoOne);
                handlePhotoFile(photoTwo);
                handlePhotoFile(photoThree);
                handlePhotoFile(photoFour);
                handlePhotoFile(photoFive);
                gameService.editGame(game);

                if(uploadedPhoto) {
                    showPhotos.update(target, photosToManage);
                }
                target.add(ManagePhotoPanel.this);
            }
        });
        managePhotos.add(new UploadProgressBar("progress", managePhotos, photoOne));

        add(managePhotos);

        setOutputMarkupId(true);
    }

    private void handlePhotoFile(FileUploadField uploadedPhoto) {
        if(uploadedPhoto.getFileUpload() == null) {
            return;
        }

        String filePath = FileUtils.saveImageFileAndReturnPath(uploadedPhoto.getFileUpload(), uploadedPhoto.getFileUpload().getClientFileName(), 480,240);
        Image image = new Image();
        image.setPath(filePath);

        Game game = model.getObject();

        Photo photo = new Photo();
        photo.setImage(image);
        photo.setVersion(1);
        photo.setAuthor(1);
        photo.setGame(game);

        if(game.getPhotos() == null){
            game.setPhotos(new ArrayList<Photo>());
        }
        gameService.editGame(game);
        game.getPhotos().add(photo);
        gameService.editGame(game);
    }

    private FileUploadField addPhoto(String id){
        FileUploadField actualPhotoUpload = new FileUploadField(id);
        actualPhotoUpload.setOutputMarkupId(true);
        managePhotos.add(actualPhotoUpload);
        return actualPhotoUpload;
    }

    protected void onConfigure() {
        setVisibilityAllowed(show);
    }
}
