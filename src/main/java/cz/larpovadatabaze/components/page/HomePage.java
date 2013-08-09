package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.components.panel.game.AddGamePanel;
import cz.larpovadatabaze.components.panel.home.LastCommentsPanel;
import cz.larpovadatabaze.components.panel.home.LastGamesPanel;
import cz.larpovadatabaze.components.panel.home.RandomLarpPanel;
import cz.larpovadatabaze.components.panel.home.StatisticsPanel;

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
}
