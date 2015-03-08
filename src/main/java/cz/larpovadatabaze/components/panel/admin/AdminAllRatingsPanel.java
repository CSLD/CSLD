package cz.larpovadatabaze.components.panel.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.larpovadatabaze.components.common.BookmarkableLinkWithLabel;
import cz.larpovadatabaze.components.common.RatingDeleteButton;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.services.CsldUserService;

/**
 *
 */
public class AdminAllRatingsPanel extends Panel {
    @SpringBean
    CsldUserService csldUserService;

    private IModel<Game> gameModel;
    private OrderedDetachableRatingModel orderedDetachableRatingModel;

    public AdminAllRatingsPanel(String id, IModel<Game> gameModel) {
        super(id);
        this.gameModel = gameModel;
    }

    private class OrderedDetachableRatingModel extends AbstractReadOnlyModel<List<Rating>> {
        private List<Rating> ratings;

        @Override
        public List<Rating> getObject() {
            if(ratings == null) recompute();
            return ratings;
        }

        public void recompute() {
            List<Rating> ratings = gameModel.getObject().getRatings();
            Collections.sort(ratings, new Comparator<Rating>() {
                @Override
                public int compare(Rating o1, Rating o2) {
                    return o2.getRating() - o1.getRating();
                }
            });
            this.ratings = ratings;
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        orderedDetachableRatingModel = new OrderedDetachableRatingModel();
        add(new ListView<Rating>("listUsers", orderedDetachableRatingModel) {
            @Override
            protected void populateItem(ListItem<Rating> item) {
                Rating rating = item.getModelObject();
                CsldUser user = rating.getUser();

                PageParameters params = new PageParameters();
                params.set("id", user.getId());
                item.add(new BookmarkableLinkWithLabel("userDetail", UserDetail.class,
                        Model.of(user.getPerson().getName()), Model.of(params)));

                item.add(new RatingDeleteButton("ratingHiddenButton", item.getModel(), gameModel));

                item.add(new Label("rating", Model.of(rating.getRating())));
            }
        }.setOutputMarkupId(true));
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        orderedDetachableRatingModel.recompute();

        setVisible(csldUserService.isLoggedAtLeastEditor());
    }
}
