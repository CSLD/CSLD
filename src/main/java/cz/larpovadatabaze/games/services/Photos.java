package cz.larpovadatabaze.games.services;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Photo;
import cz.larpovadatabaze.common.services.CRUDService;
import org.apache.commons.fileupload.FileItem;

import java.util.List;

/**
 *
 */
public interface Photos extends CRUDService<Photo, Integer> {
    int MAX_PHOTO_WIDTH = 1600;
    int MAX_PHOTO_HEIGHT = 1600;
    int PREVIEW_SIZE = 190;

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

    /**
     * Replace who added the photos added by specific user by nobody
     *
     * @param toRemove User whose photos we need to clean.
     */
    void removeAddedBy(CsldUser toRemove);
}
