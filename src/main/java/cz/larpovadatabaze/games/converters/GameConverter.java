package cz.larpovadatabaze.games.converters;

import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts Game from autoComplete String and from Game to String.
 * Unique identifier for Game is its name.
 */
public class GameConverter implements IConverter<Game> {
    private TokenSearch tokenSearch;

    public GameConverter(TokenSearch tokenSearch) {
        this.tokenSearch = tokenSearch;
    }

    @Override
    public Game convertToObject(String gameName, Locale locale) {
        List<Game> foundGames = tokenSearch.findGames(gameName);
        int amountOfGames = foundGames.size();
        if (amountOfGames == 1) {
            return foundGames.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String convertToString(Game game, Locale locale) {
        String stringRepresentation = game.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
