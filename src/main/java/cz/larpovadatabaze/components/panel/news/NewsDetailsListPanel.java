package cz.larpovadatabaze.components.panel.news;

import cz.larpovadatabaze.components.common.icons.UserIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.News;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.NewsService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
 *
 */
public class NewsDetailsListPanel extends Panel {
    @SpringBean
    private NewsService news;
    private final int SHOW_IN_PANEL = 4;
    private final int MAX_CHARS_IN_NEWS = 160;
    private Integer userId = null;
    private ModalWindow createGroupModal;

    public NewsDetailsListPanel(String id, Integer userId) {
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
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            Date dateOfNews = pieceOfNews.getAdded();

            PageParameters userParams = new PageParameters();
            userParams.add("id", author.getId());
            final BookmarkablePageLink<CsldBasePage> authorLink =
                    new BookmarkablePageLink<CsldBasePage>("authorLink", UserDetailPage.class, userParams);
            item.add(authorLink);

            final UserIcon authorsAvatar = new UserIcon("avatar", new AbstractReadOnlyModel<CsldUser>() {
                @Override
                public CsldUser getObject() {
                    return item.getModelObject().getAuthor();
                }
            });
            authorLink.add(authorsAvatar);

            authorLink.add(new Label("nick", Model.of(author.getPerson().getNickNameView())));
            authorLink.add(new Label("name", Model.of(author.getPerson().getName())));

            Label newsDate = new Label("newsDate", Model.of(formatDate.format(dateOfNews)));
            item.add(newsDate);

            Label newsContent = new Label("newsContent", Model.of(pieceOfNews.getText()));
            newsContent.setEscapeModelStrings(false);
            item.add(newsContent);


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
            item.add(updateLink);
            CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
            if(userId == null || logged == null || !userId.equals(logged.getId())) {
                updateLink.setVisibilityAllowed(false);
            }
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add list of News.
        List<News> toShow = new ArrayList<>(news.allForUser(userId));
        add(new NewsView("newsList", toShow));

        // Add modal window for updating or creating news.
        createGroupModal = new ModalWindow("updateNews");
        createGroupModal.setTitle("Novinky");
        createGroupModal.setCookieName("create-news");
        add(createGroupModal);
    }
}
