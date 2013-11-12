package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.ImageDAO;
import cz.larpovadatabaze.entities.Image;
import cz.larpovadatabaze.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.4.13
 * Time: 19:02
 */
@Repository
public class ImageServiceImpl implements ImageService {
    @Autowired
    ImageDAO imageDAO;

    @Override
    public boolean insert(Image image) {
        return imageDAO.saveOrUpdate(image);
    }

    @Override
    public List<Image> getAll() {
        return imageDAO.findAll();
    }

    @Override
    public List<Image> getUnique(Image example) {
        return imageDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(Image toRemove) {
        imageDAO.makeTransient(toRemove);
    }

    @Override
    public List<Image> getFirstChoices(String startsWith, int maxChoices) {
        throw new UnsupportedOperationException("This does not support autocompletion");
    }
}
