package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 16:59
 */
public class SortableGameProvider extends SortableDataProvider<Game, String> {
    private GameService gameService;
    public SortableGameProvider(GameService gameService){
        this.gameService = gameService;
        setSort("name", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Game> iterator(long l, long l1) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        if(property.equals("name")){
            return gameService.getOrderedByName().iterator();
        } else if(property.equals("rating")) {
            return gameService.getRated().iterator();
        } else if(property.equals("ratingAmount")) {
            return gameService.getRatedAmount().iterator();
        } else {
            return gameService.getCommentedAmount().iterator();
        }
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
