package cz.larpovadatabaze.components.page.game;

import org.apache.wicket.Session;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.ListGamesWithAnnotations;
import cz.larpovadatabaze.models.FilterGame;
import cz.larpovadatabaze.providers.SortableGameProvider;
import cz.larpovadatabaze.services.GameService;

/**
 *
 */
public class ListLastGames extends CsldBasePage {
    @SpringBean
    private GameService gameService;

    public ListLastGames(){
        SortableGameProvider sgp = new SortableGameProvider(FilterGame.OrderBy.ADDED_DESC, Session.get().getLocale());
        add(new ListGamesWithAnnotations("lastGamesAnnotated", sgp));
    }
}
