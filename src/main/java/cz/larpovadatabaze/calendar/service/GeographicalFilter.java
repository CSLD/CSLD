package cz.larpovadatabaze.calendar.service;

import cz.larpovadatabaze.calendar.Area;
import cz.larpovadatabaze.calendar.Location;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.LiteCoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GeographicalFilter implements Serializable {
    private URL filePath;

    public GeographicalFilter(URL filePath) {
        this.filePath = filePath;
    }

    public Collection<Area> areas() {
        try {
            File file = new File(this.filePath.toURI());
            Map<String, Object> map = new HashMap<>();
            map.put("url", file.toURI().toURL());
            map.put("charset", "UTF-8");

            DataStore dataStore = DataStoreFinder.getDataStore(map);
            String typeName = dataStore.getTypeNames()[0];

            FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore
                    .getFeatureSource(typeName);
            Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

            Collection<Area> areas = new ArrayList<>();

            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
            try (FeatureIterator<SimpleFeature> features = collection.features()) {
                while (features.hasNext()) {
                    SimpleFeature feature = features.next();
                    areas.add(new Area(
                            feature.getAttribute("NAME_1").toString(),
                            feature.getDefaultGeometryProperty().getValue().toString())
                    );
                }
            }
            return areas;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isGeometryInArea(Area area, Location point) {
        try {
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

            WKTReader reader = new WKTReader(geometryFactory);
            Geometry geom = reader.read(area.getGeometry());
            Point pointGeom = new Point(new LiteCoordinateSequence(point.getLongitude(), point.getLatitude()), geometryFactory);
            return geom.contains(pointGeom);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
