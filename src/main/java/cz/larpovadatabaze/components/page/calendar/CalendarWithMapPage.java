package cz.larpovadatabaze.components.page.calendar;

import cz.larpovadatabaze.components.page.CsldBasePage;
import org.apache.wicket.model.LoadableDetachableModel;
import org.wicketstuff.gmap.GMap;
import org.wicketstuff.gmap.api.GLatLng;

/**
 * Page containing view displaying all currently filtered events on the Map.
 */
public class CalendarWithMapPage extends CsldBasePage {
    @Override
    protected void onInitialize() {
        super.onInitialize();

        GMap map = new GMap("map");
        map.setStreetViewControlEnabled(false);
        map.setScaleControlEnabled(true);
        map.setScrollWheelZoomEnabled(true);
        map.setCenter(new GLatLng(52.47649, 13.228573));
        add(map);
    }
}
