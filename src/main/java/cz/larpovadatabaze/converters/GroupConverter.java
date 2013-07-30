package cz.larpovadatabaze.converters;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.exceptions.WrongParameterException;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.util.convert.IConverter;

import java.util.List;
import java.util.Locale;

/**
 * It converts CsldGroup from AutoCompletable String to object and the other way round.
 * Unique for CsldGroup is its name.
 */
public class GroupConverter implements IConverter<CsldGroup> {
    private GroupService groupService;
    public GroupConverter(GroupService groupService){
        this.groupService = groupService;
    }

    @Override
    public CsldGroup convertToObject(String groupName, Locale locale) {
        try {
            List<CsldGroup> foundGroups = groupService.getByAutoCompletable(groupName);
            int amountOfGroups = foundGroups.size();
            if(amountOfGroups == 1) {
                return foundGroups.get(0);
            } else {
                return null;
            }
        } catch(WrongParameterException ex) {
            return null;
        }
    }

    @Override
    public String convertToString(CsldGroup csldGroup, Locale locale) {
        return csldGroup.getAutoCompleteData();
    }
}
