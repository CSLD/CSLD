package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.Filter;
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

    public SortableGameProvider(GameService gameService){
        this.gameService = gameService;
        setSort("rating", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Game> iterator(long first, long amountPerPage) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        Long firstL = (Long) first;
        if(property.equals("form.wholeName")){
            return Filter.filterGames(filterGame, filterLabels, setStart(gameService.getOrderedByName(first, amountPerPage), firstL.intValue())).iterator();
        } else if(property.equals("rating")) {
            return Filter.filterGames(filterGame, filterLabels, setStart(gameService.getRated(first, amountPerPage), firstL.intValue())).iterator();
        } else if(property.equals("ratingAmount")) {
            return Filter.filterGames(filterGame, filterLabels, setStart(gameService.getRatedAmount(first, amountPerPage), firstL.intValue())).iterator();
        } else {
            return Filter.filterGames(filterGame, filterLabels, setStart(gameService.getCommentedAmount(first, amountPerPage), firstL.intValue())).iterator();
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
        return gameService.getAmountOfGames();
    }

    @Override
    public IModel<Game> model(Game game) {
        return new Model<Game>(game);
    }
}
