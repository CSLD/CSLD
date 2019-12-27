package cz.larpovadatabaze.calendar;

import java.io.Serializable;

/**
 * Created by jbalhar on 28. 9. 2016.
 */
public class Area implements Serializable {
    private String area;
    private String geometry;

    public Area(String area, String geometry) {
        this.area = area;
        this.geometry = geometry;
    }

    public String getArea() {
        return area;
    }

    public String getGeometry() {
        return geometry;
    }
}
