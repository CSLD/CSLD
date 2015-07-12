package cz.larpovadatabaze.services;

import cz.larpovadatabaze.entities.News;

import java.util.List;

/**
 * Simple service passing data through to the DAO layer.
 */
public interface NewsService {

    List<News> getLastNews(int showInPanel);

    List<News> allForUser(Integer userId);

    boolean saveOrUpdate(News pieceOfNews);
}
