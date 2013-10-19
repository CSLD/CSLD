package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.10.13
 * Time: 15:57
 */
public class ListLastGames extends CsldBasePage {
    @SpringBean
    private GameService gameService;

    public ListLastGames(){
        SortableGameProvider sgp = new SortableGameProvider(gameService, "added");
        add(new ListGamesWithAnnotations("lastGamesAnnotated", sgp));
    }
}
