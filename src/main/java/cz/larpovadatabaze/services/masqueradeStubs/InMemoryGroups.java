package cz.larpovadatabaze.services.masqueradeStubs;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.CsldGroups;
import cz.larpovadatabaze.services.Images;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.List;

public class InMemoryGroups extends InMemoryCrud<CsldGroup, Integer> implements CsldGroups {
    @Override
    public List<CsldGroup> getByAutoCompletable(String groupName) throws WrongParameterException {
        return null;
    }

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
