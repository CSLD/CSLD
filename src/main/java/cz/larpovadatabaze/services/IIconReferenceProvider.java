package cz.larpovadatabaze.services;

import org.apache.wicket.request.resource.ResourceReference;

/**
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 15:42
 */
public interface IIconReferenceProvider<O> {
    /**
     * @return Resource reference for icons
     */
    ResourceReference getIconReference();
}
