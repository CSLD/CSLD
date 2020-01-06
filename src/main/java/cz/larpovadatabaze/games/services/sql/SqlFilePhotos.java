package cz.larpovadatabaze.games.services.sql;

import cz.larpovadatabaze.common.api.GenericHibernateDAO;
import cz.larpovadatabaze.common.dao.builder.GenericBuilder;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Image;
import cz.larpovadatabaze.common.entities.Photo;
import cz.larpovadatabaze.common.services.FileService;
import cz.larpovadatabaze.common.services.ImageResizingStrategyFactoryService;
import cz.larpovadatabaze.common.services.sql.CRUD;
import cz.larpovadatabaze.games.services.Games;
import cz.larpovadatabaze.games.services.Photos;
import org.apache.commons.fileupload.FileItem;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class SqlFilePhotos extends CRUD<Photo, Integer> implements Photos {
    private final int MAX_PHOTOS_PER_GAME = 10;

    private FileService fileService;
    private Games games;
    private ImageResizingStrategyFactoryService imageResizingStrategyFactoryService;

    @Autowired
    public SqlFilePhotos(SessionFactory sessionFactory, FileService fileService, Games games, ImageResizingStrategyFactoryService imageResizingStrategyFactoryService) {
        super(new GenericHibernateDAO<>(sessionFactory, new GenericBuilder<>(Photo.class)));
        this.fileService = fileService;
        this.games = games;
        this.imageResizingStrategyFactoryService = imageResizingStrategyFactoryService;
    }

    @Override
    public void remove(Photo toRemove) {
        crudRepository.delete(toRemove);

        // Delete file(s)
        if (toRemove.getImage() != null) {
            fileService.removeFiles(toRemove.getImage().getPath());
        }
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

        games.saveOrUpdate(game);

        return true;
    }

    @Override
    public List<Photo> getRandomPhotos(int amount) {
        Criteria criteria = crudRepository.getExecutableCriteria();
        criteria.add(Restrictions.sqlRestriction("1=1 order by random()"));
        criteria.setMaxResults(amount);
        return criteria.list();
    }
}
