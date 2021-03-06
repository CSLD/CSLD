package cz.larpovadatabaze.users.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.CsldGroups;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class InMemoryGroups extends InMemoryCrud<CsldGroup, Integer> implements CsldGroups {
    @Override
    public ResourceReference getIconReference() {
        return new ResourceReference(Images.class, "Image") {
            @Override
            public IResource getResource() {
                return (IResource) attributes -> {
                };
            }
        };
    }
}
