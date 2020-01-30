package cz.larpovadatabaze.administration.model;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Person;

import java.io.Serializable;

/**
 * Created by jbalhar on 12/13/2014.
 */
public class UserRatesOwnGameDto implements Serializable {
    public final int gameId;
    public final int userId;
    public final String gameName;
    public final String userName;
    public final String userEmail;

    public UserRatesOwnGameDto(Game game, CsldUser user) {
        this.gameId = game.getId();
        this.userId = user.getId();
        this.gameName = game.getName();
        Person person = user.getPerson();
        this.userName = person.getName();
        this.userEmail = person.getEmail();
    }
}
