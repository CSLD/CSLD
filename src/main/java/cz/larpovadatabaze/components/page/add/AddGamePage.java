package cz.larpovadatabaze.components.page.add;

import cz.larpovadatabaze.components.form.AddGameForm;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 25.4.13
 * Time: 16:11
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
        if(game == null) {
            game = new Game();
        }

        add(new AddGameForm("addGame", game));
    }
}
