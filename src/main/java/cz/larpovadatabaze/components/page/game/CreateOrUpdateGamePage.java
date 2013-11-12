package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.CreateOrUpdateGamePanel;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.GameService;
import cz.larpovadatabaze.utils.HbUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 *  This page allows User to create new or update existing game.
 */
@AuthorizeInstantiation({"User","Editor","Admin"})
public class CreateOrUpdateGamePage extends CsldBasePage {
    @SpringBean
    GameService gameService;

    public CreateOrUpdateGamePage(PageParameters params){
        Game game = null;
        if(!params.isEmpty()){
            Integer id = params.get("id").to(Integer.class);
            game = gameService.getById(id);
            if(HbUtils.isProxy(game)){
                game = HbUtils.deproxy(game);
            }
        }

        add(new CreateOrUpdateGamePanel("createOrUpdateGame", game){
            @Override
            protected void onCsldAction(AjaxRequestTarget target, Form<?> form) {
                super.onCsldAction(target, form);

                Game game = (Game) form.getModelObject();
                PageParameters params = new PageParameters();
                params.add("id", game.getId());
                CsldAuthenticatedWebSession session = (CsldAuthenticatedWebSession) CsldAuthenticatedWebSession.get();
                session.requestClear();
                throw new RestartResponseException(GameDetail.class, params);
            }
        });
    }
}
