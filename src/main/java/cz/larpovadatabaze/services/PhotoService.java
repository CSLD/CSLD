package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Photo;
import org.apache.wicket.util.upload.FileItem;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 6.9.13
 * Time: 22:58
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
     * @return
     */
    boolean createNewPhotoForGame(Game game, FileItem fileItem);
}
