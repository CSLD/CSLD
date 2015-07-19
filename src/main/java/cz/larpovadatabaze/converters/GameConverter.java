package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.GameService;
import org.apache.wicket.util.convert.IConverter;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * It converts Game from autoComplete String and from Game to String.
 * Unique identifier for Game is its name.
 */
public class GameConverter implements IConverter<Game> {
    private GameService gameService;
    public GameConverter(GameService gameService){
        this.gameService = gameService;
    }

    @Override
    public Game convertToObject(String gameName, Locale locale) {
        try {
            Collection<Game> foundGames = gameService.getByAutoCompletable(gameName);
            int amountOfGames = foundGames.size();
            if(amountOfGames == 1) {
                return foundGames.iterator().next();
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
