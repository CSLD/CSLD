package cz.larpovadatabaze.dto;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.entities.Game;

/**
 * Created by jbalhar on 12/13/2014.
 */
public class UserRatesOwnGameDto extends BaseDto {
    private int gameId;
    private int userId;
    private String gameName;
    private String userName;
    private String userEmail;

    public UserRatesOwnGameDto() {
    }

    public UserRatesOwnGameDto(int gameId, int userId, String gameName, String userName, String userEmail) {
        this.gameId = gameId;
        this.userId = userId;
        this.gameName = gameName;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public int getGameId() {
        return gameId;
    }

    public int getUserId() {
        return userId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
