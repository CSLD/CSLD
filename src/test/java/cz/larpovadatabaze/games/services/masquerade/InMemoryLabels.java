package cz.larpovadatabaze.games.services.masquerade;

import cz.larpovadatabaze.common.entities.CsldUser;
import cz.larpovadatabaze.common.entities.Label;
import cz.larpovadatabaze.common.services.masquerade.InMemoryCrud;
import cz.larpovadatabaze.games.services.Labels;

import java.util.List;
import java.util.stream.Collectors;

public class InMemoryLabels extends InMemoryCrud<Label, Integer> implements Labels {
    @Override
    public List<Label> getRequired() {
        return inMemory.stream()
                .filter(label -> label.getRequired())
                .collect(Collectors.toList());
    }

    @Override
    public List<Label> getOptional() {
        return inMemory.stream()
                .filter(label -> !label.getRequired())
                .collect(Collectors.toList());
    }

    @Override
    public List<Label> getAuthorizedOptional(CsldUser authorizedTo) {
        return getOptional();
    }

    @Override
    public List<Label> getAuthorizedRequired(CsldUser authorizedTo) {
        return getRequired();
    }
}
