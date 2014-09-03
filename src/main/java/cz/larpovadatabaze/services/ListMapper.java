package cz.larpovadatabaze.services;

import org.dozer.Mapper;

import java.util.List;

/**
 * Interface providing capabilities of mapping list of objects of one type on list of objects of other objects.
 */
public interface ListMapper extends Mapper {
    /**
     * Performs mapping from List of objects of one type to list of object of different type. All objects must be
     * of same type.
     *
     * @param source List of source objects.
     * @param destinationClass All members of result list, will be instances of this class.
     * @param <T> Type of the class
     * @return List of mapped objects.
     */
    <T> List<T> mapList(List source, Class<T> destinationClass);
}
