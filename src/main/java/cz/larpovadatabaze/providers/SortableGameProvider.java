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
    public Iterator<? extends Game> iterator(long first, long last) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        int amountOfGames = gameService.getAll().size();
        if(amountOfGames > last) {
            last = amountOfGames;
        }
        if(property.equals("form.wholeName")){
            return Filter.filterGames(filterGame, filterLabels,gameService.getOrderedByName().subList((int)first,(int)last)).iterator();
        } else if(property.equals("rating")) {
            return Filter.filterGames(filterGame, filterLabels,gameService.getRated().subList((int)first,(int)last)).iterator();
        } else if(property.equals("ratingAmount")) {
            return Filter.filterGames(filterGame, filterLabels,gameService.getRatedAmount().subList((int)first,(int)last)).iterator();
        } else {
            return Filter.filterGames(filterGame, filterLabels,gameService.getCommentedAmount().subList((int)first,(int)last)).iterator();
        }
    }

    public void setFilters(FilterGame filters, List<Label> labels) {
        filterGame = filters;
        filterLabels = labels;
        setSort("rating", SortOrder.ASCENDING);
    }

    @Override
    public long size() {
        return gameService.getAll().size();
    }

    @Override
    public IModel<Game> model(Game game) {
        return new Model<Game>(game);
    }
}
