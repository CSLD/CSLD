package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Photo;
import org.apache.wicket.util.upload.FileItem;

import java.util.List;

/**
 *
 */
public interface PhotoService extends GenericService<Photo> {
    public static final int MAX_PHOTO_WIDTH = 1600;
    public static final int MAX_PHOTO_HEIGHT = 1600;
    public static final int PREVIEW_SIZE = 190;

    boolean saveOrUpdate(Photo actualPhoto);

    /**
     * @return Photo by id
     */
    Photo get(int id);

    /**
     * Create new photo for given game (from file upload)
     *
     * @param game Game to create photo for
     * @param fileItem File item from
     *
     * @return True when photo added, false when maximum number of photos reached.
     */
    boolean createNewPhotoForGame(Game game, FileItem fileItem);

    /**
     * It returns given amount of random photos from the database.
     *
     * @param amount
     */
    List<Photo> getRandomPhotos(int amount);
}
