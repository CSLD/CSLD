package cz.larpovadatabaze.games.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Photo;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.services.Photos;
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