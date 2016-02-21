package cz.larpovadatabaze.calendar;

/**
 * Bounding Box representing area.
 */
public class BoundingBox {
    private Location bottomLeft;
    private Location upperTop;

    public BoundingBox(Location bottomLeft, Location upperTop) {
        this.bottomLeft = bottomLeft;
        this.upperTop = upperTop;
    }

    public Boolean isInArea(Location toTest) {
        return toTest.getLatitude() >= bottomLeft.getLatitude() &&
                toTest.getLatitude() <= upperTop.getLatitude() &&
                toTest.getLongitude() >= bottomLeft.getLongitude() &&
                toTest.getLongitude() <= upperTop.getLongitude();
    }
}
