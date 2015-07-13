package cz.larpovadatabaze.entities;

import org.apache.wicket.request.resource.IResource;

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
    private IResource image;

    public Advertisement(Integer gameId, IResource image) {
        this.gameId = gameId;
        this.image = image;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public IResource getImage() {
        return image;
    }

    public void setImage(IResource image) {
        this.image = image;
    }
}
