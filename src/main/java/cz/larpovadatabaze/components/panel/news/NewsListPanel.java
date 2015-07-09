package cz.larpovadatabaze.components.panel.news;

import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * Panel showing last four news.
 */
public class NewsListPanel extends Panel {
    public NewsListPanel(String id) {
        super(id);
    }

    private class NewsView extends ListView<News> {
        public NewsView(String id, List<? extends News> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(ListItem<News> item) {
            News news = item.getModelObject();
            CsldUser author = news.getAuthor();

            Fragment f = new Fragment("pieceOfNews", "newsFragment", NewsListPanel.this);
            item.add(f);

            WebMarkupContainer newsWrapper = new WebMarkupContainer("newsWrapper");
            f.add(newsWrapper);

            Label newsContent = new Label("newsContent", Model.of(news.getText()));
            newsContent.setEscapeModelStrings(false);
            newsWrapper.add(newsContent);


            PageParameters userParams = new PageParameters();
            userParams.add("id", author.getId());
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
