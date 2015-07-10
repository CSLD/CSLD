package cz.larpovadatabaze.components.panel.news;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.services.NewsService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Panel showing last four news.
 */
public class NewsListPanel extends Panel {
    @SpringBean
    private NewsService news;
    private final int SHOW_IN_PANEL = 4;

    public NewsListPanel(String id) {
        super(id);
    }

    private class NewsView extends ListView<News> {
        public NewsView(String id, List<? extends News> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<News> item) {
            News news = item.getModelObject();
            CsldUser author = news.getAuthor();

            Fragment f = new Fragment("pieceOfNews", "newsFragment", NewsListPanel.this);
            item.add(f);

            WebMarkupContainer newsWrapper = new WebMarkupContainer("newsWrapper");
            f.add(newsWrapper);

            Label newsContent = new Label("newsContent", Model.of(news.getText()));
            newsContent.setEscapeModelStrings(false);
            newsWrapper.add(newsContent);

            // User icon
            final UserIcon commenterIcon = new UserIcon("newsCreatorIcon", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getAuthor();
                }
            });
            f.add(commenterIcon);

            PageParameters userParams = new PageParameters();
            userParams.add("id", author.getId());
            final BookmarkablePageLink<CsldBasePage> newsCreatorLink =
                    new BookmarkablePageLink<CsldBasePage>("newsCreatorLink", UserDetailPage.class, userParams);
            Label commenterName = new Label("newsCreatorName", Model.of(author.getPerson().getNickNameView()));
            newsCreatorLink.add(commenterName);
            f.add(newsCreatorLink);

            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfPublish = new Date();
            dateOfPublish.setTime(news.getAdded().getTime());
            Label commentDate = new Label("newsDate", Model.of(formatDate.format(dateOfPublish)));
            f.add(commentDate);

        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<News> toShow = new ArrayList<News>(news.getLastNews(SHOW_IN_PANEL));
        add(new NewsView("visibleNews", toShow));
    }
}
