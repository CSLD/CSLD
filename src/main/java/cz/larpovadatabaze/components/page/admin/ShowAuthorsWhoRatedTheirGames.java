package cz.larpovadatabaze.components.page.admin;

import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.dao.CsldUserDAO;
import cz.larpovadatabaze.dto.UserRatesOwnGameDto;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Page for showing authors who rated their own games. Ti simplify work of editors.
 */
@AuthorizeInstantiation({"Editor","Admin"})
public class ShowAuthorsWhoRatedTheirGames extends CsldBasePage {
    @SpringBean
    private CsldUserDAO authors;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<UserRatesOwnGameDto> usersWhoRatesOwnGames = authors.getUsersWhoRatesOwnGames();
        ListView<UserRatesOwnGameDto> userRatesOwnGameDtoListView = new ListView<UserRatesOwnGameDto>("authorsRatedOwnGames", usersWhoRatesOwnGames) {
            @Override
            protected void populateItem(ListItem<UserRatesOwnGameDto> item) {
                UserRatesOwnGameDto userRatesOwnGameDto = item.getModel().getObject();

                item.add(new Label("email", userRatesOwnGameDto.getUserEmail()));
                item.add(new BookmarkableLinkWithLabel("user", UserDetail.class, Model.of(userRatesOwnGameDto.getUserName()),
                        Model.of(new PageParameters().set("id", userRatesOwnGameDto.getUserId()))));
                item.add(new BookmarkableLinkWithLabel("game", GameDetail.class, Model.of(userRatesOwnGameDto.getGameName()),
                        Model.of(new PageParameters().set("id", userRatesOwnGameDto.getGameId()))));
            }
        };

        add(userRatesOwnGameDtoListView);
    }
}
