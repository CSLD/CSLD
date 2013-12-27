package cz.larpovadatabaze.components.panel;

import cz.larpovadatabaze.entities.Image;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 *
 */
public class ImagePanel extends FormComponentPanel<Image> {
    private Image baseImage;

    public ImagePanel(String id) {
        super(id);

        IModel<List<FileUpload>> listIModel = new IModel<List<FileUpload>>(){
            private List<FileUpload> uploadedFields;

            @Override
            public List<FileUpload> getObject() {
                return uploadedFields;
            }

            @Override
            public void setObject(List<FileUpload> object) {
                this.uploadedFields = object;
            }

            @Override
            public void detach() {
            }
        };
        FileUploadField fileUploadField = new FileUploadField("imageP", listIModel);
        add(fileUploadField);
    }

    @Override
    protected void convertInput() {
        super.convertInput();
        if(baseImage == null) {
            this.baseImage = getModel().getObject();
        }

        if(((FileUploadField) get("imageP")).getFileUploads() != null){

            Image image = new Image();
            image.setFileUpload(((FileUploadField) get("imageP")).getFileUploads());
            setConvertedInput(image);
            getModel().setObject(image);
        } else {
            getModel().setObject(baseImage);
        }
    }
}
