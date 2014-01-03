package cz.larpovadatabaze.components.common.icons;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:54
 */
public class GameIcon extends AbstractCsldIcon<GameService, Game> {
    @SpringBean
    GameService gameService;

    public GameIcon(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected GameService getService() {
        return gameService;
    }
}
