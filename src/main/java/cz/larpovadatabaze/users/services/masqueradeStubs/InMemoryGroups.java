package cz.larpovadatabaze.users.services.masqueradeStubs;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.common.exceptions.WrongParameterException;
import cz.larpovadatabaze.common.services.masqueradeStubs.InMemoryCrud;
import cz.larpovadatabaze.games.services.Images;
import cz.larpovadatabaze.users.services.CsldGroups;
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
