package cz.larpovadatabaze.utils;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.Label;
import cz.larpovadatabaze.models.FilterGame;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Filter {
    public static ArrayList<Game> filterGames(FilterGame filters, List<Label> labels, List<Game> toFilter) {
        ArrayList<Game> filteredList = new ArrayList<Game>();
        for(Game game: toFilter) {
            if(game.getLabels() == null) {
                game.setLabels(new ArrayList<cz.larpovadatabaze.entities.Label>());
            }
            if(game.getHours() == null){
                game.setHours(0);
            }
            if(game.getDays() == null){
                game.setDays(0);
            }
            if(game.getPlayers() == null){
                game.setPlayers(0);
            }
            boolean containsLabels = game.getLabels().containsAll(labels);
            if(containsLabels){
                filteredList.add(game);
            }
        }
        return filteredList;
    }
}
