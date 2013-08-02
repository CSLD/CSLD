package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class AddGamePage extends CsldBasePage {
    @SpringBean
    private GameService gameService;

    public AddGamePage(PageParameters params){
        Game game = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            game = gameService.getById(id);
            if(HbUtils.isProxy(game)){
                game = HbUtils.deproxy(game);
            }
        }

        add(new CreateOrUpdateGamePanel("createOrUpdateGame", game));
    }
}
