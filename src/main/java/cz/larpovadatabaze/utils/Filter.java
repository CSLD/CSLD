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
    public static List<Game> filterGames(FilterGame filters, List<Label> labels, List<Game> toFilter) {
        List<Game> filteredList = new ArrayList<Game>();
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
            if(filters.getMaxDays() == null) {
                filters.setMaxDays(10000000d);
            }
            if(filters.getMaxHours() == null) {
                filters.setMaxHours(10000000d);
            }
            if(filters.getMaxPlayers() == null) {
                filters.setMaxPlayers(10000000d);
            }
            boolean containsLabels = game.getLabels().containsAll(labels);
            boolean correctHours = game.getHours() >= filters.getMinHours() && game.getHours() <= filters.getMaxHours();
            boolean correctDays = game.getDays() >= filters.getMinDays() && game.getDays() <= filters.getMaxDays();
            boolean correctPlayers = game.getPlayers() >= filters.getMinPlayers() && game.getPlayers() <= filters.getMaxPlayers();
            if(containsLabels && correctDays && correctHours && correctPlayers){
                filteredList.add(game);
            }
        }
        return filteredList;
    }
}
