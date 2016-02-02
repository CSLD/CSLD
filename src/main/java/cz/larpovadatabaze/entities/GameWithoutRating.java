package cz.larpovadatabaze.entities;

/**
 * Wrapper for game that has no rating to be able to be shown together with games with rating
 *
 * User: Michal Kara Date: 14.3.15 Time: 10:05
 */
public class GameWithoutRating implements IGameWithRating {

    private final Game game;

    public GameWithoutRating(Game game) {
        this.game = game;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Integer getRating() {
        // No rating
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameWithoutRating that = (GameWithoutRating) o;

        return !(game != null ? !game.equals(that.game) : that.game != null);

    }

    @Override
    public int hashCode() {
        return game != null ? game.hashCode() : 0;
    }
}
