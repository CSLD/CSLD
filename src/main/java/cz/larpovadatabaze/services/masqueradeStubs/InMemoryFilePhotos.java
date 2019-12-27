package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.Photos;
import org.apache.commons.fileupload.FileItem;

import java.util.List;

public class InMemoryFilePhotos extends InMemoryCrud<Photo, Integer> implements Photos {
    @Override
    public boolean createNewPhotoForGame(Game game, FileItem fileItem) {
        return false;
    }

    @Override
    public List<Photo> getRandomPhotos(int amount) {
        return null;
    }
}
