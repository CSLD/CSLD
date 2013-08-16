package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.VideoDAO;
import cz.larpovadatabaze.entities.Video;
import cz.larpovadatabaze.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public class VideoServiceImpl implements VideoService{
    @Autowired
    VideoDAO videoDAO;

    @Override
    public List<Video> getAll() {
        return videoDAO.findAll();
    }

    @Override
    public List<Video> getUnique(Video example) {
        return videoDAO.findByExample(example, new String[]{});
    }

    @Override
    public void remove(Video toRemove) {
        videoDAO.makeTransient(toRemove);
    }

    @Override
    public boolean saveOrUpdate(Video video) {
        return videoDAO.saveOrUpdate(video);
    }
}
