package cz.larpovadatabaze.components.page.search;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AbstractListGamePanel;
import cz.larpovadatabaze.components.panel.search.SearchBoxPanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.Strings;

/**
 *
 */
public class SearchResultsPage extends CsldBasePage {

    @SpringBean
    private GameService gameService;

    public SearchResultsPage(PageParameters params) {
        String query = params.get(SearchBoxPanel.QUERY_PARAMETER_NAME).toString();

        final Component gamesResultsPanel = new AbstractListGamePanel<String>("games", Model.of(query)) {
            @Override
            protected SortableDataProvider<Game, String> getDataProvider() {
                return new GameProvider(getModelObject());
            }
        };
        gamesResultsPanel.setOutputMarkupId(true);
        add(gamesResultsPanel);

/*
        final UserResultsPanel userResultsPanel = new UserResultsPanel("users", query);
        userResultsPanel.setOutputMarkupId(true);
        add(userResultsPanel);
        */
    }

    /**
     * Provides games matching the query.
     * TODO Currently gets all games and does the filtering itself - consider rewriting to some more efficient approach - TODO
     */
    private class GameProvider extends SortableDataProvider<Game, String> {

        private final String query;

        /**
         * List of games - not to be serialized
         */
        private transient List<Game> filteredGames;

        private GameProvider(String query) {
            this.query = query;
        }

        private List<Game> getGameList() {
            if (filteredGames == null) {
                // Load games
                List<Game> allResults = gameService.getAll();
                filteredGames = new ArrayList<Game>();
                Collator collator = Collator.getInstance(new Locale("cs"));
                collator.setStrength(Collator.PRIMARY);

                for(Game result: allResults){
                    if(Strings.containsIgnoreCaseAndAccents(result.getAutoCompleteData(), query)) {
                        filteredGames.add(result);
                    }
                }
            }

            return filteredGames;
        }

        @Override
        public Iterator<? extends Game> iterator(long first, long count) {
            return getGameList().subList((int)first, (int)(first+count)).iterator();
        }

        @Override
        public long size() {
            return getGameList().size();
        }

        @Override
        public IModel<Game> model(Game object) {
            return new Model<Game>(object);
        }
    }
}
