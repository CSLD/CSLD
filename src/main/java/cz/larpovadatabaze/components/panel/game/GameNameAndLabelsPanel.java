package cz.larpovadatabaze.components.panel.game;

import cz.larpovadatabaze.components.common.AbstractCsldPanel;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.page.game.GameDetail;
import cz.larpovadatabaze.entities.Game;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Panel to show game name (as a link to game detail) and labels - this fragment is used on various list pages
 *
 * User: Michal Kara Date: 5.4.15 Time: 9:06
 */
public class GameNameAndLabelsPanel extends AbstractCsldPanel<Game> {
    public GameNameAndLabelsPanel(String id, IModel<Game> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final BookmarkablePageLink<CsldBasePage> gameLink =
            new BookmarkablePageLink<CsldBasePage>("gameLink", GameDetail.class, GameDetail.paramsForGame(getModelObject()));
        final Label nameLabel = new Label("gameName", Model.of(getModelObject().getName()));
        gameLink.add(nameLabel);
        add(gameLink);

        StringBuilder labels = new StringBuilder();
        for(cz.larpovadatabaze.entities.Label l : getModelObject().getLabels()) {
            if (labels.length() > 0) {
                // Add divisor
                labels.append(", ");
            }
            labels.append(l.getName());
        }
        add(new Label("labels", labels.toString()));

    }
}
