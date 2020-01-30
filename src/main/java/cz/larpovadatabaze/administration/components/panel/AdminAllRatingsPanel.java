package cz.larpovadatabaze.administration.components.panel;

import cz.larpovadatabaze.administration.components.RatingDeleteButton;
import cz.larpovadatabaze.common.components.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Rating;
import cz.larpovadatabaze.games.services.Ratings;
import cz.larpovadatabaze.users.components.page.UserDetailPage;
import cz.larpovadatabaze.users.services.AppUsers;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 */
public class AdminAllRatingsPanel extends Panel {
    @SpringBean
    AppUsers users;
    @SpringBean
    Ratings ratings;

    private IModel<Game> gameModel;
    private OrderedDetachableRatingModel orderedDetachableRatingModel;

    public AdminAllRatingsPanel(String id, IModel<Game> gameModel) {
        super(id);
        this.gameModel = gameModel;
    }

    private class OrderedDetachableRatingModel implements IModel<List<Rating>> {
        private List<Rating> currentRatings;

        @Override
        public List<Rating> getObject() {
            if (currentRatings == null) recompute();
            return currentRatings;
        }

        public void recompute() {
            currentRatings = ratings.getRatingsOfGame(gameModel.getObject());
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        orderedDetachableRatingModel = new OrderedDetachableRatingModel();
        add(new ListView<>("listUsers", orderedDetachableRatingModel) {
            @Override
            protected void populateItem(ListItem<Rating> item) {
                Rating rating = item.getModelObject();
                CsldUser user = rating.getUser();

                PageParameters params = new PageParameters();
                params.set("id", user.getId());
                item.add(new BookmarkableLinkWithLabel("userDetail", UserDetailPage.class,
                        Model.of(user.getPerson().getName()), Model.of(params)));

                item.add(new RatingDeleteButton("ratingHiddenButton", item.getModel(), gameModel));

                item.add(new Label("rating", Model.of(rating.getRating())));
            }
        }.setOutputMarkupId(true));
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        orderedDetachableRatingModel.recompute();

        setVisible(users.isAtLeastEditor());
    }
}
