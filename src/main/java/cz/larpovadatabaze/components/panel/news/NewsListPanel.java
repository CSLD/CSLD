package cz.larpovadatabaze.components.panel.news;

import cz.larpovadatabaze.behavior.dotdotdot.DotDotDotBehavior;
import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.components.panel.group.CreateOrUpdateGroupPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.NewsService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jsoup.Jsoup;

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
    private final int MAX_CHARS_IN_NEWS = 160;
    private Integer userId = null;
    private ModalWindow createGroupModal;

    public NewsListPanel(String id) {
        super(id);
    }

    public NewsListPanel(String id, Integer userId) {
        super(id);
        this.userId = userId;
    }

    private class NewsView extends ListView<News> {
        public NewsView(String id, List<? extends News> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<News> item) {
            final News pieceOfNews = item.getModelObject();
            CsldUser author = pieceOfNews.getAuthor();

            // Create fragment
            Fragment f = new Fragment("pieceOfNews", "newsFragment", NewsListPanel.this);
            item.add(f);

            // News wrapper
            WebMarkupContainer newsWrapper = new WebMarkupContainer("newsWrapper");
            f.add(newsWrapper);

            PageParameters userParams = new PageParameters();
            userParams.add("id", author.getId());

            // "More" link after the piece of news
            BookmarkablePageLink moreLink = new BookmarkablePageLink("moreLink", UserDetailPage.class, userParams);
            newsWrapper.add(moreLink);

            // Content
            String newsToShow = Jsoup.parse(pieceOfNews.getText()).text();
            if(newsToShow.length() > MAX_CHARS_IN_NEWS){
                newsToShow = newsToShow.substring(0, MAX_CHARS_IN_NEWS);
            }
            Label newsContent = new Label("newsContent", Model.of(newsToShow));
            newsContent.setEscapeModelStrings(false);
            newsWrapper.add(newsContent);
            newsWrapper.add(new DotDotDotBehavior().setAfterComponentId(moreLink.getMarkupId(true)));

            AjaxLink updateLink = new AjaxLink("updateNews") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    createGroupModal.setContent(new CreateOrUpdateNewsPanel(createGroupModal.getContentId(), Model.of(pieceOfNews)){
                        @Override
                        protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                            super.onCsldAction(target, form);
                            createGroupModal.close(target);
                        }
                    });
                    createGroupModal.show(target);
                }
            };
            f.add(updateLink);
            CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
            if(userId == null || logged == null || !userId.equals(logged.getId())) {
                updateLink.setVisibilityAllowed(false);
            }

            // User icon
            final UserIcon commenterIcon = new UserIcon("newsCreatorIcon", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getAuthor();
                }
            });
            f.add(commenterIcon);

            // Link and name
            final BookmarkablePageLink<CsldBasePage> newsCreatorLink =
                    new BookmarkablePageLink<CsldBasePage>("newsCreatorLink", UserDetailPage.class, userParams);
            Label commenterName = new Label("newsCreatorName", Model.of(author.getPerson().getNickNameView()));
            newsCreatorLink.add(commenterName);
            f.add(newsCreatorLink);

            // News date
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfPublish = new Date();
            dateOfPublish.setTime(pieceOfNews.getAdded().getTime());
            Label commentDate = new Label("newsDate", Model.of(formatDate.format(dateOfPublish)));
            f.add(commentDate);

        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Component header = new WebMarkupContainer("lastNews");
        add(header);

        List<News> toShow;
        if(userId == null ) {
            toShow = new ArrayList<News>(news.getLastNews(SHOW_IN_PANEL));
        } else {
            toShow = new ArrayList<News>(news.allForUser(userId));
            header.setVisibilityAllowed(false);
        }
        add(new NewsView("visibleNews", toShow));

        // Add modal window for updating or creating news.
        createGroupModal = new ModalWindow("updateNews");
        createGroupModal.setTitle("Novinky");
        createGroupModal.setCookieName("create-news");
        add(createGroupModal);
    }
}
