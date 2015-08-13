package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.NewsDAO;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.services.NewsService;
import cz.larpovadatabaze.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Balda on 10. 7. 2015.
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsDAO news;

    @Override
    public List<News> getLastNews(int showInPanel) {
        return news.getLastNews(showInPanel);
    }

    @Override
    public List<News> allForUser(Integer userId) {
        return news.allForUser(userId);
    }

    @Override
    public boolean saveOrUpdate(News pieceOfNews) {
        pieceOfNews.setAuthor(UserUtils.getLoggedUser());
        pieceOfNews.setAdded(Timestamp.from(Instant.now()));
        return news.saveOrUpdate(pieceOfNews);
    }
}
