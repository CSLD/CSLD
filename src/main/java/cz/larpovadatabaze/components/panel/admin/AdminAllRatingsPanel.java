package cz.larpovadatabaze.components.panel.admin;

import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class AdminAllRatingsPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    private IModel<Game> gameModel;

    public AdminAllRatingsPanel(String id, IModel<Game> gameModel) {
        super(id);
        this.gameModel = gameModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        List<Rating> orderedRatings = gameModel.getObject().getRatings();
        Collections.sort(orderedRatings, new Comparator<Rating>() {
            @Override
            public int compare(Rating o1, Rating o2) {
                return o2.getRating() - o1.getRating();
            }
        });
        add(new ListView<Rating>("listUsers", orderedRatings) {
            @Override
            protected void populateItem(ListItem<Rating> item) {
                Rating rating = item.getModelObject();
                CsldUser user = rating.getUser();

                PageParameters params = new PageParameters();
                params.set("id", user.getId());
                item.add(new BookmarkableLinkWithLabel("userDetail", UserDetail.class,
                        Model.of(user.getPerson().getName()), Model.of(params)));

                item.add(new Label("rating", Model.of(rating.getRating())));
            }
        });
    }

    @Override
    protected void onConfigure() {
        setVisibilityAllowed(csldUserService.isLoggedAtLeastEditor());
    }
}
