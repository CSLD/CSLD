package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 18.10.13
 * Time: 15:32
 */
public class SortableAnnotatedProvider extends SortableDataProvider<Game,String> {
    private GameService gameService;
    private CsldGroup csldGroup = null;
    private CsldUser author = null;

    public SortableAnnotatedProvider(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Iterator<? extends Game> iterator(long pFirst, long pCount) {
        int first = ((Long) pFirst).intValue();
        int count = ((Long) pCount).intValue();
        if(author != null) {
            return gameService.getGamesOfAuthor(author, first, count).iterator();
        } else if(csldGroup != null) {
            return gameService.getGamesOfGroup(csldGroup, first, count).iterator();
        } else {
            return (new ArrayList<Game>()).iterator();
        }
    }

    @Override
    public long size() {
        if(author != null) {
            return gameService.getAmountOfGamesOfAuthor(author);
        }
        if(csldGroup != null) {
            return gameService.getAmountOfGamesOfGroup(csldGroup);
        }
        return 0L;
    }

    @Override
    public IModel<Game> model(Game object) {
        return Model.of(object);
    }

    public void setAuthor(CsldUser author) {
        this.author = author;
    }

    public void setGroup(CsldGroup group) {
        this.csldGroup = group;
    }
}
