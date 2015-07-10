package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Balda on 10. 7. 2015.
 */
@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private CsldUserServiceImpl users;

    @Override
    public List<News> getLastNews(int showInPanel) {
        CsldUser author = users.getAll().get(0);

        News first = new News();
        first.setText("First piece of news.");
        first.setAuthor(author);
        first.setAdded(Timestamp.from(Instant.now()));

        News second = new News();
        second.setText("Second piece of News");
        second.setAuthor(author);
        second.setAdded(Timestamp.from(Instant.now()));

        List<News> news = new ArrayList<News>();
        Collections.addAll(news, first, second);
        return news;
    }
}
