package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.PhotoDAO;
import cz.larpovadatabaze.entities.Photo;
import cz.larpovadatabaze.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 6.9.13
 * Time: 22:58
 */
@Repository
public class PhotoServiceImpl implements PhotoService {
    @Autowired
    private PhotoDAO photoDao;

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
    }
}
