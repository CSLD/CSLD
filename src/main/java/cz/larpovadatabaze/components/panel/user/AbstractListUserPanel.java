package cz.larpovadatabaze.components.panel.user;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetailPage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.CommentService;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * It contains all games in a pageable list, there are four possible ways to order
 * the list. Order alphabetically, Order by rating or order by amount of ratings, or
 * by amount of comments.
 */
public abstract class AbstractListUserPanel<T> extends AbstractCsldPanel<T> {
    private SortableDataProvider<CsldUser, String> sdp;

    public AbstractListUserPanel(String id, IModel<T> model) {
        super(id, model);
    }

    /**
     * @return Data provider
     */
    protected abstract SortableDataProvider<CsldUser, String> getDataProvider();

    @Override
    protected void onInitialize() {
        super.onInitialize();

        sdp = getDataProvider();
        final DataView<CsldUser> propertyList = new DataView<CsldUser>("listUsers", sdp) {
            @Override
            protected void populateItem(Item<CsldUser> item) {
                CsldUser user = item.getModelObject();

                final BookmarkablePageLink<CsldBasePage> userLink =
                        new BookmarkablePageLink<>("userLink", UserDetailPage.class, UserDetailPage.paramsForUser(user));
                final Label nameLabel = new Label("userName", Model.of(user.getPerson().getName()));
                userLink.add(nameLabel);
                item.add(userLink);

                final Label nickNameLabel = new Label("userNickname", Model.of(user.getPerson().getNickNameView()));
                item.add(nickNameLabel);

                final Label ageLabel = new Label("userAge", Model.of(user.getPerson().getAge()));
                item.add(ageLabel);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        PagingNavigator paging = new PagingNavigator("navigator", propertyList);
        add(paging);
    }
}
