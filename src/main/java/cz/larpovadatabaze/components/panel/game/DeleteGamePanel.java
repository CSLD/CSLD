package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.security.CsldRoles;
import cz.larpovadatabaze.services.GameService;

/**
 * This panel contains link for deleting the game.
 */
public class DeleteGamePanel extends Panel {
    @SpringBean
    private GameService gameService;

    private final int gameId;
    private IModel<String> deletedGameLabelModel;

    // It would probably be better, if I created some model, and linked one element to this link, which will change its content based
    // on the actual state of the model, as well as behavior will be changed according to that model.
    public DeleteGamePanel(String id, int gameId) {
        super(id);
        this.gameId = gameId;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PageParameters params = new PageParameters();
        params.add("id", gameId);

        deletedGameLabelModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return new StringResourceModel(gameService.isHidden(gameId)?"game.show":"game.delete", DeleteGamePanel.this, null).toString();
            }
        };
        Label deleteGameLabel = new Label("deleteGameLabel", deletedGameLabelModel);
        deleteGameLabel.setOutputMarkupId(true);

        AjaxLink<CsldBasePage> deleteGame = new AjaxLink<CsldBasePage>("deleteGame") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                gameService.toggleGameState(gameId);
                deletedGameLabelModel.detach();
                ajaxRequestTarget.add(DeleteGamePanel.this);
            }
        };
        deleteGame.add(deleteGameLabel);
        deleteGame.setOutputMarkupId(true);

        add(deleteGame);
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        CsldUser logged = CsldAuthenticatedWebSession.get().getLoggedUser();
        boolean visible = false;
        if(logged != null){
            if(logged.getRole() > CsldRoles.USER.getRole()) {
                visible = true;
            }
        }
        setVisibilityAllowed(visible);
    }
}
