package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;
import cz.larpovadatabaze.entities.UserPlayedGame;

import java.util.ArrayList;
import java.util.Collection;

/**
 * It filters all events from the source by the games that are wanted by the user.
 */
public class EventsForWantedGames implements Events {
    private CsldUser user;
    private Events events;

    public EventsForWantedGames(CsldUser user, Events events) {
        this.user = user;
        this.events = events;
    }

    @Override
    public void store(Event event) {
        events.store(event);
    }

    @Override
    public void delete(Event event) {
        events.delete(event);
    }

    @Override
    public Collection<Event> all() {
        Collection<Event> all = events.all();
        Collection<Event> filtered = new ArrayList<>();
        Collection<Game> wantsToPlay = new ArrayList<>();
        for(UserPlayedGame stateOfGame: user.getPlayedGames()) {
            if(stateOfGame.getStateEnum() == UserPlayedGame.UserPlayedGameState.WANT_TO_PLAY) {
                wantsToPlay.add(stateOfGame.getGame());
            }
        }

        for(Event event: all) {
            for(Game game: event.getGames()){
                if(wantsToPlay.contains(game)) {
                    filtered.add(event);
                    break;
                }
            }
        }

        return filtered;
    }
}
