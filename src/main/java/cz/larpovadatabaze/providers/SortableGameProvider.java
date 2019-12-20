package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.services.Games;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class SortableGameProvider extends SortableDataProvider<Game, String> {

    @SpringBean
    private Games games;

    private IModel<FilterGame> filterModel;

    public SortableGameProvider(IModel<FilterGame> filterModel) {
        Injector.get().inject(this);
        this.filterModel = filterModel;
    }

    @Override
    public Iterator<? extends Game> iterator(long first, long amountPerPage) {
        Long firstL = first;

        return setStart(
                games.getFilteredGames(
                        filterModel.getObject(),
                        firstL.intValue(),
                        ((Long) amountPerPage).intValue()),
                firstL.intValue()
        ).iterator();
    }

    private List<Game> setStart(List<Game> games, int first){
        for(Game game: games){
            game.setFirst(first);
        }
        return games;
    }

    @Override
    public long size() {
        return games.getAmountOfFilteredGames(filterModel.getObject());
    }

    @Override
    public IModel<Game> model(Game game) {
        return new Model<>(game);
    }
}
