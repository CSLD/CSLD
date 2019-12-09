package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.dao.NewsDAO;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.services.NewsService;
import cz.larpovadatabaze.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * SQL Implementation of the relevant pieces of news.
 */
@Service
@Transactional
public class SqlNews implements NewsService {
    private NewsDAO news;

    @Autowired
    public SqlNews(NewsDAO news) {
        this.news = news;
    }

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
