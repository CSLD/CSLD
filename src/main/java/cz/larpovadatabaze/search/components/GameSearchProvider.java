package cz.larpovadatabaze.search.components;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.utils.HbUtils;
import cz.larpovadatabaze.common.utils.Strings;
import cz.larpovadatabaze.games.services.Games;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.*;

/**
 * Provides games matching the query.
 * TODO Currently gets all games and does the filtering itself - consider rewriting to some more efficient approach - TODO
 */
public class GameSearchProvider extends SortableDataProvider<Game, String> {

    private static final int DEFAULT_MAX_RESULTS = 50;
    /**
     * List of games - not to be serialized
     */
    private transient List<Game> filteredGames;

    @SpringBean
    private Games games;

    private String query;

    private int maxResults = DEFAULT_MAX_RESULTS;

    private boolean moreAvailable;

    // TODO: Remove duplication.
    private class GameModel extends LoadableDetachableModel<Game> {

        // Game id. We could also store id as page property.
        private int gameId;

        private GameModel(int gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Game load() {
            Game game = games.getById(gameId);
            if(HbUtils.isProxy(game)){
            }    game = HbUtils.deproxy(game);

            return game;
        }
    }


    public GameSearchProvider() {
        Injector.get().inject(this);
    }

    public void setQuery(String query) {
        this.query = query;
        this.filteredGames = null; // Clear games
    }

    public List<Game> getGameList() {
        if (filteredGames == null) {
            // Load games
            List<Game> allResults = new ArrayList<>(new LinkedHashSet<>(games.getAll()));
            filteredGames = new ArrayList<>();
            Collator collator = Collator.getInstance(new Locale("cs"));
            collator.setStrength(Collator.PRIMARY);

            moreAvailable = false;
            for (Game result : allResults) {
                if (Strings.containsIgnoreCaseAndAccents(result.getAutoCompleteData(), query)) {
                    if (filteredGames.size() >= maxResults) {
                        // Got enough...
                        moreAvailable = true;
                        break;
                    }
                    filteredGames.add(result);
                }
            }
        }

        return filteredGames;
    }

    @Override
    public Iterator<? extends Game> iterator(long first, long count) {
        return getGameList().subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return getGameList().size();
    }

    @Override
    public IModel<Game> model(Game object) {
        return new GameModel(object.getId());
    }

    /**
     * @param maxResults Maximum results returned
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * @return Whether there is more results available
     */
    public boolean isMoreAvailable() {
        return moreAvailable;
    }
}
