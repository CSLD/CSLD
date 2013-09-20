package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.LastGamesPanel;
import cz.larpovadatabaze.components.panel.home.RandomLarpPanel;
import cz.larpovadatabaze.components.panel.home.StatisticsPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

/**
 *
 */
public class HomePage extends CsldBasePage {
    public HomePage(){
        add(new LastGamesPanel("lastGames"));
        add(new LastCommentsPanel("lastComments"));

        add(new AddGamePanel("createGamePanel"));
        add(new RandomLarpPanel("randomLarpPanel"));
        add(new StatisticsPanel("statisticsPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery-1.8.3.js"));
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery-ui-1.9.2.custom.js"));
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery.nivo.slider.pack.js"));
        response.render(JavaScriptHeaderItem.forUrl("/files/js/jquery.nivo.slider.js"));
    }
}
