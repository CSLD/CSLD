package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.PhotoDAO;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.upload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class PhotoServiceImpl implements PhotoService {
    private final int MAX_PHOTOS_PER_GAME = 10;

    @Autowired
    private PhotoDAO photoDao;

    @Autowired
    private FileService fileService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    @Override
    public boolean saveOrUpdate(Photo actualPhoto) {
        return photoDao.saveOrUpdate(actualPhoto);
    }

    @Override
    public List<Photo> getAll() {
        return photoDao.findAll();
    }

    @Override
    public List<Photo> getUnique(Photo example) {
        return photoDao.findByExample(example, new String[]{});
    }

    @Override
    public void remove(Photo toRemove) {
        photoDao.makeTransient(toRemove);

        // Delete file(s)
        if (toRemove.getImage() != null) {
            fileService.removeFiles(toRemove.getImage().getPath());
        }
    }

    @Override
    public List<Photo> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }

    @Override
    public Photo get(int id) {
        return photoDao.findById(id);
    }

    /**
     * @param photos List of photos
     *
     * @return Next order id for the photo list
     */
    private int nextOrderSeq(List<Photo> photos) {
        int max = 0;

        if (photos == null) return 0;

        for(Photo p : photos) {
            max = Math.max(max, p.getOrderSeq());
        }

        return max+1;
    }

    @Override
    public boolean createNewPhotoForGame(Game game, FileItem fileItem) {
        FileService.ResizeAndSaveReturn ret = fileService.saveImageFileAndPreviewAndReturnPath(new FileUpload(fileItem), imageResizingStrategyFactoryService.getMaxWidthHeightStrategy(MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT), imageResizingStrategyFactoryService.getCuttingSquareStrategy(PREVIEW_SIZE, 50));
        Image image = new Image();
        image.setPath(ret.path);
        image.setContentType(fileItem.getContentType());

        if (game.getPhotos().size() >= MAX_PHOTOS_PER_GAME) return false; // No more photos can be added

        Photo photo = new Photo();
        photo.setImage(image);
        photo.setAuthor(1);
        photo.setGame(game);
        photo.setOrderSeq(nextOrderSeq(game.getPhotos()));
        photo.setFullWidth(ret.savedWidth);
        photo.setFullHeight(ret.savedHeight);

        game.getPhotos().add(photo);

        gameService.saveOrUpdate(game);

        return true;
    }

    @Override
    public List<Photo> getRandomPhotos(int amount) {
        return photoDao.getRandom(amount);
    }
}
