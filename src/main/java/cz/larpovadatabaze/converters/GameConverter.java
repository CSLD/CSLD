package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.Games;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts Game from autoComplete String and from Game to String.
 * Unique identifier for Game is its name.
 */
public class GameConverter implements IConverter<Game> {
    private Games games;

    public GameConverter(Games games) {
        this.games = games;
    }

    @Override
    public Game convertToObject(String gameName, Locale locale) {
        try {
            List<Game> foundGames = games.getByAutoCompletable(gameName);
            int amountOfGames = foundGames.size();
            if (amountOfGames == 1) {
                return foundGames.get(0);
            } else {
                return null;
            }
        } catch(WrongParameterException ex) {
            return null;
        }
    }

    @Override
    public String convertToString(Game game, Locale locale) {
        String stringRepresentation = game.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
