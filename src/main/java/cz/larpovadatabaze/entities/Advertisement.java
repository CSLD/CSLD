package cz.larpovadatabaze.entities;

import java.io.Serializable;

/**
 * Advertisement on the main page
 *
 * User: Michal Kara Date: 7.3.15 Time: 18:45
 */
public class Advertisement implements Serializable {
    /**
     * ID of game to link
     */
    private Integer gameId;

    /**
     * Image of the advertisement
     */
    private String image;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
