package cz.larpovadatabaze.components.panel.game;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * The Played Panel has three states of being. Either the player does not have any
 * interest in game or the player played the game, or the player is interested in
 * playing the game.
 */
public class PlayedPanel extends Panel {
    public PlayedPanel(String id) {
        super(id);
    }
}
