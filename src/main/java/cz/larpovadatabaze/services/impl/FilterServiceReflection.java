package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.services.FilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class FilterServiceReflection implements FilterService {
    private final static Logger log = LoggerFactory.getLogger(FilterServiceReflection.class);

    @Override
    public Collection filterByPropertyName(Collection toFilter, String propertyName, Object propertyValue) {
        Collection result = new ArrayList();
        for(Object filteredObject: toFilter) {
            Field filtered = ReflectionUtils.findField(filteredObject.getClass(),propertyName);
            ReflectionUtils.makeAccessible(filtered);
            try {
                if(filtered.get(filteredObject).equals(propertyValue)){
                    //noinspection unchecked
                    result.add(filteredObject);
                }
            } catch (IllegalAccessException e) {
                log.info("Filtered object didn't contain propertyName used.");
            }
        }
        return result;
    }
}
