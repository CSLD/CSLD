package cz.larpovadatabaze.games.components.panel;

import cz.larpovadatabaze.common.components.fileupload.FileUploadComponentPanel;
import cz.larpovadatabaze.common.components.fileupload.IFileUploadCallback;
import cz.larpovadatabaze.common.components.gallery.GalleryPanel;
import cz.larpovadatabaze.common.components.gallery.IGalleryDataProvider;
import cz.larpovadatabaze.common.components.gallery.IGalleryManager;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Photo;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Photos;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

/**
 * Panel for game photos
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 15:21
 */
public class PhotoPanel extends Panel {
    private final static Logger logger = Logger.getLogger(PhotoPanel.class);

    @SpringBean
    private Games games;
    @SpringBean
    private Photos sqlPhotos;
    @SpringBean
    private FileService fileService;

    private WebMarkupContainer wrapper;

    private FeedbackPanel feedbackPanel;

    private List<String> storedErrors = new ArrayList<String>();

    private static class PhotoImageInfo extends GalleryPanel.ImageInfo {
        private final int orderSeq;

        public PhotoImageInfo(int id, String description, Boolean isTitle, int fullWidth, int fullHeight, int orderSeq) {
            super(id, description, isTitle, fullWidth, fullHeight);
            this.orderSeq = orderSeq;
        }
    }

    /**
     * Model to access photos of the game
     */
    private class PhotoListModel extends LoadableDetachableModel<List<? extends GalleryPanel.ImageInfo>> {
        @Override
        public List<? extends GalleryPanel.ImageInfo> load() {
            // Create info from game photos
            List<PhotoImageInfo> res = new ArrayList<PhotoImageInfo>();

            // Create intermediate objects
            for(Photo p : ((Game)getDefaultModelObject()).getPhotos()) {
                res.add(new PhotoImageInfo(p.getId(), p.getDescription(), null, p.getFullWidth(), p.getFullHeight(), p.getOrderSeq()));
            }

            // Sort
            Collections.sort(res, new Comparator<PhotoImageInfo>() {
                @Override
                public int compare(PhotoImageInfo o1, PhotoImageInfo o2) {
                    if (o1.orderSeq < o2.orderSeq) return -1;
                    if (o1.orderSeq > o2.orderSeq) return 1;
                    if (o1.getId() < o2.getId()) return -1;
                    if (o1.getId() > o2.getId()) return 1;
                    return 0;
                }
            });

            return res;
        }
    }

    /**
     * Gallery provider for game photos
     */
    private class PhotoGalleryDataProvider implements IGalleryDataProvider {

        @Override
        public AbstractResource.ResourceResponse getImagePreview(int photoId) {
            // Load photo
            Photo p = sqlPhotos.getById(photoId);

            // Try if preview exists
            String path = fileService.getFilePreviewInDataDir(p.getImage().getPath());
            if (path == null) {
                // Use full image
                path = fileService.getPathInDataDir(p.getImage().getPath());
            }
            return fileService.respondWithFile(path, p.getImage().getContentType());
        }

        @Override
        public AbstractResource.ResourceResponse getFullImage(int photoId) {
            // Load photo
            Photo p = sqlPhotos.getById(photoId);

            // Send full path
            String path = fileService.getPathInDataDir(p.getImage().getPath());
            return fileService.respondWithFile(path, p.getImage().getContentType());
        }
    }

    /**
     * Provide gallery management functions
     */
    private class PhotoGalleryManager implements IGalleryManager {
        @Override
        public void setImageDescription(int imageId, String newDescription) {
            Photo p = sqlPhotos.getById(imageId);
            p.setDescription(newDescription);
            sqlPhotos.saveOrUpdate(p);
        }

        @Override
        public void deleteImage(int imageId) {
            Photo p = sqlPhotos.getById(imageId);
            sqlPhotos.remove(p);
        }

        @Override
        public void setImageOrder(Iterable<Integer> imageIds) {
            Game g = (Game)getDefaultModelObject();

            // Build map by id
            Map<Integer, Photo> photoMap = new HashMap<Integer, Photo>();
            for(Photo p : g.getPhotos()) photoMap.put(p.getId(), p);

            // Build new list
            List<Photo> newPhotos = new ArrayList<Photo>();
            for(Integer id : imageIds) {
                Photo p = photoMap.get(id);
                if (p != null) {
                    // Photo exists - set new order when it is different
                    int newOrder = newPhotos.size();
                    if (p.getOrderSeq() != newOrder) p.setOrderSeq(newOrder);
                    newPhotos.add(p);

                    photoMap.remove(id); // Remove from map
                }
            }

            // Add remaining photos
            newPhotos.addAll(photoMap.values());

            // Set and save
            g.setPhotos(newPhotos);
            games.saveOrUpdate(g);
        }

        @Override
        public void publishPhoto(int id) {
            Photo p = sqlPhotos.getById(id);
            p.setFeatured(true);
            sqlPhotos.saveOrUpdate(p);
        }

        @Override
        public void hidePhotoFromFront(int id) {
            Photo p = sqlPhotos.getById(id);
            p.setFeatured(false);
            sqlPhotos.saveOrUpdate(p);
        }
    }

    public PhotoPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Game game = (Game) getDefaultModelObject();
        boolean canEdit = games.canEditGame(game);

        wrapper = new WebMarkupContainer("wrapper"); // Wrapper so that we can refresh ourselves
        wrapper.setOutputMarkupId(true);
        add(wrapper);

        /* Gallery */

        // Add gallery
        GalleryPanel gallery = new GalleryPanel("galleryPanel", new PhotoListModel(), new PhotoGalleryDataProvider(), canEdit?new PhotoGalleryManager():null);
        wrapper.add(gallery);

        // Feedback panel
        feedbackPanel = new FeedbackPanel("feedbackPanel");
        wrapper.add(feedbackPanel);

        /* Upload panel */
        if (canEdit) {
            // Create real upload panel
            wrapper.add(new FileUploadComponentPanel("uploadPanel", 5000000, "/(\\.|\\/)(gif|jpe?g|png)$/i", new IFileUploadCallback() {
                @Override
                public void filesUploaded(List<FileItem> fileItems) {
                    for(FileItem fi : fileItems) {
                        try {
                            // Check type
                            String ct1 = fi.getContentType().split("/")[0];
                            if (!"image".equals(ct1)) {
                                storedErrors.add(fi.getName() + ": Soubor není obrázek");
                                continue;
                            }

                            // Create photo
                            if (!sqlPhotos.createNewPhotoForGame((Game) PhotoPanel.this.getDefaultModelObject(), fi)) {
                                storedErrors.add(fi.getName() + ": Dosažen maximální počet obrázků ke hře");
                            }
                        }
                        catch(Exception e) {
                            logger.error("Error while adding photo", e);
                            storedErrors.add(fi.getName() + ": Interní chyba");
                        }
                    }
                }

                @Override
                public void fileUploadDone(AjaxRequestTarget target) {
                    // Add stored errors
                    for(String error : storedErrors) {
                        getPage().error(error);
                    }
                    storedErrors.clear();

                    // Refresh complete panel
                    target.add(wrapper);
                }
            }));

            if (StringUtils.isNotBlank(game.getPhotoAuthor())) {
                // Author filled in - hide warning
                wrapper.add(new WebMarkupContainer("photoWarning").setVisible(false));
            }
            else {
                // Add warning
                wrapper.add(new WebMarkupContainer("photoWarning"));
            }
        }
        else {
            // Empty upload panel
            wrapper.add(new WebMarkupContainer("uploadPanel"));

            // Empty warning
            wrapper.add(new WebMarkupContainer("photoWarning").setVisible(false));
        }
    }
}