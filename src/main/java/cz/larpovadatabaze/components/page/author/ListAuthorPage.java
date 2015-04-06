package cz.larpovadatabaze.components.page.author;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.DecimalFormat;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.user.UserDetail;
import cz.larpovadatabaze.components.panel.author.AuthorsPanel;
import cz.larpovadatabaze.components.panel.game.GameNameAndLabelsPanel;
import cz.larpovadatabaze.components.panel.game.GameRatingBoxPanel;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.providers.SortableAuthorProvider;
import cz.larpovadatabaze.services.CsldUserService;

/**
 *
 */
public class ListAuthorPage extends CsldBasePage {
    @SpringBean
    CsldUserService csldUserService;

    private final static DecimalFormat ratingFormat = new DecimalFormat("0.0");

    @SuppressWarnings("unchecked")
    public ListAuthorPage(){
        SortableAuthorProvider sap = new SortableAuthorProvider(csldUserService);
        final DataView<CsldUser> propertyList = new DataView<CsldUser>("listAuthor",sap) {
            @Override
            protected void populateItem(Item<CsldUser> item) {
                CsldUser actualAuthor = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", actualAuthor.getId());

                final BookmarkablePageLink<CsldUser> authorName =
                        new BookmarkablePageLink<CsldUser>("authorName", UserDetail.class, params);
                final Label nicknameLabel = new Label("authorNicknameContent", actualAuthor.getPerson().getNickNameView());
                final Label nameLabel = new Label("authorNameContent", actualAuthor.getPerson().getName());
                authorName.add(nameLabel);
                authorName.add(nicknameLabel);
                item.add(authorName);

                item.add(new Label("amountOfCreated", actualAuthor.getAmountOfCreated()));

                Game bestGame = actualAuthor.getBestGame();

                WebMarkupContainer bestGameWrapper = new WebMarkupContainer("bestGameWrapper");
                item.add(bestGameWrapper);

                if (bestGame == null) {
                    // No best game (?)
                    bestGameWrapper.setVisible(false);
                }
                else {
                    bestGameWrapper.add(new GameRatingBoxPanel("ratingBox", Model.of(bestGame)));

                    // Name and labels
                    bestGameWrapper.add(new GameNameAndLabelsPanel("nameAndLabels", Model.of(bestGame)));

                    // Year
                    bestGameWrapper.add(new Label("year", bestGame.getYear()));

                    // Amount of ratings
                    bestGameWrapper.add(new Label("ratings", bestGame.getAmountOfRatings()));

                    // Amount of comments
                    bestGameWrapper.add(new Label("comments", bestGame.getAmountOfComments()));
                }
            }
        };
        propertyList.setOutputMarkupId(true);
        propertyList.setItemsPerPage(25L);

        add(propertyList);
        add(new PagingNavigator("navigator", propertyList));

        /*
        add(new OrderByBorder("orderByName", "form.wholeName", sap)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });

        add(new OrderByBorder("orderByBestGame", "bestGame.name", sap)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged()
            {
                propertyList.setCurrentPage(0);
            }
        });
        */

        add(new AuthorsPanel("addAuthorPanel"));
    }
}
