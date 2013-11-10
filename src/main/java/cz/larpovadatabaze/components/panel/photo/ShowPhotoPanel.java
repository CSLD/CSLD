package cz.larpovadatabaze.components.panel.photo;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.entities.Photo;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

/**
 * It shows all photos added to given game.
 */
public class ShowPhotoPanel extends Panel {
    private List<Photo> photosToShow;

    public ShowPhotoPanel(String id, List<Photo> photos) {
        super(id);

        photosToShow = photos;
        ListView<Photo> photosL = new ListView<Photo>("photos", photos){
            @Override
            protected void populateItem(ListItem<Photo> item) {
                Photo photo = item.getModelObject();
                if(photo.getImage() == null) {
                    return;
                }

                Image photoImage = new Image("photo",
                        new PackageResourceReference(Csld.class, photo.getImage().getPath()));
                item.add(photoImage);
            }
        };
        photosL.setOutputMarkupId(true);
        add(photosL);

        setOutputMarkupId(true);
    }

    public void update(AjaxRequestTarget target, List<Photo> newPhotos){
        photosToShow.removeAll(photosToShow);
        photosToShow.addAll(newPhotos);

        target.add(this);
    }
}
