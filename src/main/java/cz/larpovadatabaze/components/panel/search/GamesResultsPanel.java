package cz.larpovadatabaze.components.panel.search;

import cz.larpovadatabaze.components.common.icons.GameIcon;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Rating;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.services.ImageService;
import cz.larpovadatabaze.utils.Filter;
import cz.larpovadatabaze.utils.Strings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * It shows game results of search.
 */
public class GamesResultsPanel extends Panel {
    @SpringBean
    GameService gameService;

    @SpringBean
    ImageService imageService;

    private ArrayList<Game> fullResults;
    private ArrayList<Game> shortResults;
    private String query;

    private ListView<Game> othersList;
    private ListView<Game> fullList;

    public GamesResultsPanel(String id, String query) {
        super(id);

        if(query == null) {
            query = "";
        }
        this.query = query;

        fillShortFull(query);

        fullList = new ListView<Game>("fullGames", fullResults) {
            @Override
            protected void populateItem(ListItem<Game> item) {
                Game game = item.getModelObject();

                PageParameters gameParams = GameDetail.paramsForGame(game);

                final BookmarkablePageLink<CsldBasePage> gameLink =
                        new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, gameParams);
                final GameIcon gameLinkImage = new GameIcon("gameLinkImage", item.getModel());
                gameLink.add(gameLinkImage);
                item.add(gameLink);

                String gameRatingColor = Rating.getColorOf(game.getAverageRating());
                Label gameRating = new Label("gameRating","");
                gameRating.add(new AttributeAppender("class", Model.of(gameRatingColor), " "));
                item.add(gameRating);

                final BookmarkablePageLink<CsldBasePage> gameLinkContent =
                        new BookmarkablePageLink<CsldBasePage>("gameLinkContent", GameDetail.class, gameParams);
                final Label gameName = new Label("gameName", game.getName());
                gameLinkContent.add(gameName);
                item.add(gameLinkContent);
            }
        };
        add(fullList);

        add(new WebMarkupContainer("moreResults").setVisible(!shortResults.isEmpty()));

        othersList = new ListView<Game>("shortGames", shortResults) {
            @Override
            protected void populateItem(ListItem<Game> item) {
                Game game = item.getModelObject();

                PageParameters params = new PageParameters();
                params.add("id", game.getId());

                final BookmarkablePageLink<CsldBasePage> authorShortLink =
                        new BookmarkablePageLink<CsldBasePage>("shortGameLink", GameDetail.class, params);
                final Label shortName = new Label("shortGameName", game.getName());
                final Label shortYear = new Label("shortGameYear", game.getYear());
                authorShortLink.add(shortName);
                authorShortLink.add(shortYear);
                item.add(authorShortLink);
            }
        };
        add(othersList);
    }

    private void fillShortFull(String query) {
        List<Game> allResults = gameService.getAll();
        List<Game> searchResults = new ArrayList<Game>();
        Collator collator = Collator.getInstance(new Locale("cs"));
        collator.setStrength(Collator.PRIMARY);

        for(Game result: allResults){
            if(Strings.containsIgnoreCaseAndAccents(result.getAutoCompleteData(), query)) {
                searchResults.add(result);
            }
        }

        if(searchResults.size() > 2){
            fullResults = new ArrayList<Game>(new ArrayList<Game>(searchResults.subList(0,3)));
            shortResults = new ArrayList<Game>(new ArrayList<Game>(searchResults.subList(3, searchResults.size())));
        } else {
            fullResults = new ArrayList<Game>(new ArrayList<Game>(searchResults.subList(0,searchResults.size())));
            shortResults = new ArrayList<Game>();
        }
    }

    public void reload(AjaxRequestTarget target, FilterGame filters, List<cz.larpovadatabaze.entities.Label> allLabels) {
        fillShortFull(query);

        fullResults = Filter.filterGames(filters, allLabels, fullResults);
        fullList.setList(fullResults);
        shortResults = Filter.filterGames(filters, allLabels, shortResults);
        othersList.setList(shortResults);

        List<Game> toRemove = new ArrayList<Game>();
        if(fullResults.size() < 3 && shortResults.size() > 0){
            for(Game game: shortResults) {
                fullResults.add(game);
                toRemove.add(game);
                if(fullResults.size() >= 3) {
                    break;
                }
            }
            shortResults.removeAll(toRemove);
        }

        target.add(this);
    }
}
