package cz.larpovadatabaze.components.page.user;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.user.UsersPanel;
import cz.larpovadatabaze.entities.Comment;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.UserPlayedGame;
import cz.larpovadatabaze.providers.SortableUserProvider;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListUser extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    public ListUser(){
        Image usersIcon = new Image("usersIcon", new ContextRelativeResource(cz.larpovadatabaze.entities.Image.getLightBulbIconPath()));
        add(usersIcon);

        SortableUserProvider sup = new SortableUserProvider(csldUserService);
        final DataView<CsldUser> propertyList = new DataView<CsldUser>("listAuthor",sup) {
            @Override
            protected void populateItem(Item<CsldUser> item) {
                CsldUser csldUser = item.getModelObject();
                List<UserPlayedGame> played =
                        (csldUser.getPlayedGames() != null) ? csldUser.getPlayedGames(): new ArrayList<UserPlayedGame>();
                List<Comment> commented =
                        (csldUser.getCommented() != null) ? csldUser.getCommented(): new ArrayList<Comment>();

                CsldUser moderator = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", moderator.getId());
                final BookmarkablePageLink<CsldBasePage> moderatorLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("userLink", UserDetail.class, params);
                final Label moderatorNick = new Label("userNickname", moderator.getPerson().getNickname());
                final Label moderatorName = new Label("userName", moderator.getPerson().getName());
                moderatorLinkContent.add(moderatorNick);
                moderatorLinkContent.add(moderatorName);
                item.add(moderatorLinkContent);

                final Label commentedGames = new Label("commentedGames", commented.size());
                final Label playedGames = new Label("playedGames", played.size());
                item.add(commentedGames);
                item.add(playedGames);
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        add(new PagingNavigator("navigator", propertyList));

        add(new OrderByBorder("orderByName", "form.wholeName", sup)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByCommented", "comments", sup)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByPlayed", "played", sup)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new UsersPanel("userPanel"));
    }
}
