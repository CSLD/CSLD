package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class SortableGameProvider extends SortableDataProvider<Game, String> {
    private GameService gameService;
    private FilterGame filterGame = new FilterGame();
    private List<Label> filterLabels = new ArrayList<Label>();
    private int filterLabel = -1;

    public SortableGameProvider(GameService gameService, String defaultSort) {
        this.gameService = gameService;
        setSort(defaultSort, SortOrder.ASCENDING);
    }

    public SortableGameProvider(GameService gameService, Label filterLabel) {
        this(gameService, "rating");
        if(filterLabel != null) {
            filterLabels.add(filterLabel);
        }
    }

    @Override
    public Iterator<? extends Game> iterator(long first, long amountPerPage) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        Long firstL = (Long) first;
        if(property.equals("form.wholeName")){
            return setStart(
                    gameService.getFilteredGames(
                            filterGame,
                            filterLabels,
                            firstL.intValue(),
                            ((Long)amountPerPage).intValue(),
                            " order by game.name "),
                        firstL.intValue()
            ).iterator();
        } else if(property.equals("rating")) {
            return setStart(
                    gameService.getFilteredGames(
                            filterGame,
                            filterLabels,
                            firstL.intValue(),
                            ((Long)amountPerPage).intValue(),
                            " order by game.total_rating desc "),
                    firstL.intValue()
            ).iterator();
        } else if(property.equals("ratingAmount")) {
            return setStart(
                    gameService.getFilteredGames(
                            filterGame,
                            filterLabels,
                            firstL.intValue(),
                            ((Long)amountPerPage).intValue(),
                            " order by game.amount_of_ratings desc "),
                    firstL.intValue()
            ).iterator();
        } else if(property.equals("added")) {
            return setStart(
                    gameService.getFilteredGames(
                            filterGame,
                            filterLabels,
                            firstL.intValue(),
                            ((Long)amountPerPage).intValue(),
                            " order by game.added desc "),
                    firstL.intValue()
            ).iterator();
        } else {
            return setStart(
                    gameService.getFilteredGames(
                            filterGame,
                            filterLabels,
                            firstL.intValue(),
                            ((Long)amountPerPage).intValue(),
                            " order by game.amount_of_comments desc "),
                    firstL.intValue()
            ).iterator();
        }
    }

    private List<Game> setStart(List<Game> games, int first){
        for(Game game: games){
            game.setFirst(first);
        }
        return games;
    }

    public void setFilters(FilterGame filters, List<Label> labels) {
        filterGame = filters;
        filterLabels = labels;
        setSort("rating", SortOrder.ASCENDING);
    }

    @Override
    public long size() {
        return gameService.getAmountOfFilteredGames(
                filterGame,
                filterLabels
        );
    }

    @Override
    public IModel<Game> model(Game game) {
        return new Model<Game>(game);
    }
}
