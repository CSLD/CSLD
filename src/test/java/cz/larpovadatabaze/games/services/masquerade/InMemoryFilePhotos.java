package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Photo;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
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

    @Override
    public void removeAddedBy(CsldUser toRemove) {

    }
}
