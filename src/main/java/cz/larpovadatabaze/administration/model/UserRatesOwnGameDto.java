package cz.larpovadatabaze.administration.model;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Game;
import cz.larpovadatabaze.common.entities.Person;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRatesOwnGameDto that = (UserRatesOwnGameDto) o;
        return gameId == that.gameId &&
                userId == that.userId &&
                Objects.equals(gameName, that.gameName) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, userId, gameName, userName, userEmail);
    }
}
