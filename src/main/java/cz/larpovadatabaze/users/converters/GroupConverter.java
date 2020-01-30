package cz.larpovadatabaze.users.converters;

import cz.larpovadatabaze.common.entities.CsldGroup;
import cz.larpovadatabaze.search.services.TokenSearch;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts CsldGroup from AutoCompletable String to object and the other way round.
 * Unique for CsldGroup is its name.
 */
public class GroupConverter implements IConverter<CsldGroup> {
    private TokenSearch tokenSearch;

    public GroupConverter(TokenSearch tokenSearch) {
        this.tokenSearch = tokenSearch;
    }

    @Override
    public CsldGroup convertToObject(String groupName, Locale locale) {
        List<CsldGroup> foundGroups = tokenSearch.findGroups(groupName);
        int amountOfGroups = foundGroups.size();
        if (amountOfGroups == 1) {
            return foundGroups.get(0);
        } else {
            CsldGroup group = CsldGroup.getEmptyGroup();
            group.setId(-1);
            return group;
        }
    }

    @Override
    public String convertToString(CsldGroup csldGroup, Locale locale) {
        String stringRepresentation = csldGroup.getAutoCompleteData();
        return stringRepresentation != null ? stringRepresentation : "";
    }
}
