package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Game detail page using the old URL - redirect to new one
 *
 * User: Michal Kara
 * Date: 24.4.14
 * Time: 14:49
 */
public class GameDetailOld extends Page {

    @SpringBean
    GameService gameService;

    /**
     * Constructor - initialize just model
     */
    public GameDetailOld(PageParameters params) {
        super(Model.of(params.get("id").toInt()));
    }
    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Redirect to new page
        throw new RestartResponseException(GameDetail.class, GameDetail.paramsForGame(gameService.getById((Integer) getDefaultModelObject())));
    }

}
