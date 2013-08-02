package cz.larpovadatabaze.api;

import cz.larpovadatabaze.Csld;
import org.apache.wicket.resource.Properties;

/**
 * Utilize loading resources for class.
 */
public class ResourceLoader {
    public static Properties getProperties(Class<?> clazz, String path){
        return Csld.get().getResourceSettings().getPropertiesFactory().load(clazz, path);
    }
}
