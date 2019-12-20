package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.security.CsldAuthenticatedWebSession;
import cz.larpovadatabaze.services.Games;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * This panel contains link for deleting the game.
 */
public class DeleteGamePanel extends Panel {
    @SpringBean
    private Games games;

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
                return new StringResourceModel(games.isHidden(gameId) ? "game.show" : "game.delete", DeleteGamePanel.this, null).getString();
            }
        };
        Label deleteGameLabel = new Label("deleteGameLabel", deletedGameLabelModel);
        deleteGameLabel.setOutputMarkupId(true);

        AjaxLink<CsldBasePage> deleteGame = new AjaxLink<CsldBasePage>("deleteGame") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                games.toggleGameState(gameId);
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
        super.onConfigure();
        setVisibilityAllowed(CsldAuthenticatedWebSession.get().isAtLeastEditor());
    }
}
