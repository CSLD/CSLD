package cz.larpovadatabaze.games.components.icons;

import cz.larpovadatabaze.common.components.icons.AbstractCsldIcon;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.games.services.Games;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:54
 */
public class GameIcon extends AbstractCsldIcon<Games, Game> {
    @SpringBean
    Games games;

    public GameIcon(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected Games getService() {
        return games;
    }
}
