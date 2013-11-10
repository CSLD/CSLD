package cz.larpovadatabaze.components.page.game;

import cz.larpovadatabaze.Csld;
import cz.larpovadatabaze.components.page.CsldBasePage;
import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.game.FilterGamesPanel;
import cz.larpovadatabaze.components.panel.game.ListGamePanel;
import cz.larpovadatabaze.models.FilterGame;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

/**
 *
 */
public class ListGame extends CsldBasePage {
    public ListGame() {
        Image chartsIcon = new Image("chartsIcon",
                new PackageResourceReference(Csld.class, cz.larpovadatabaze.entities.Image.getChartsIconPath()));
        add(chartsIcon);

        final ListGamePanel listGamePanel = new ListGamePanel("listGame");
        listGamePanel.setOutputMarkupId(true);
        add(listGamePanel);

        add(new AddGamePanel("addGame"));
        add(new FilterGamesPanel("filterGames"){
            @Override
            public void onCsldEvent(AjaxRequestTarget target, Form<?> form, List<cz.larpovadatabaze.entities.Label> labels) {
                FilterGame filterGame = (FilterGame) form.getModelObject();
                listGamePanel.reload(target, filterGame, labels);
            }
        });
    }
}
