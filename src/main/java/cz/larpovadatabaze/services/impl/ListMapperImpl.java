package cz.larpovadatabaze.services.impl;

import cz.larpovadatabaze.services.ListMapper;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListMapperImpl extends DozerBeanMapper implements ListMapper {
    @Override
    public <T> List<T> mapList(List source, Class<T> destinationClass) {
        List<T> results = new ArrayList<T>();
        for (Object sourceObject : source) {
            results.add(map(sourceObject, destinationClass));
        }
        return results;
    }
}
