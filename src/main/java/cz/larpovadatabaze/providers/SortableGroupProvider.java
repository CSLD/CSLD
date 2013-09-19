package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.CsldGroup;
import cz.larpovadatabaze.services.GroupService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 2.5.13
 * Time: 19:46
 */
public class SortableGroupProvider extends SortableDataProvider<CsldGroup, String> {
    private GroupService groupService;

    public SortableGroupProvider(GroupService groupService){
        this.groupService = groupService;
        setSort("default",SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends CsldGroup> iterator(long first, long amountPerPage) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        if(property.equals("default")){
            return groupService.orderedByName(first, amountPerPage).iterator();
        } else {
            return groupService.orderedByName(first, amountPerPage).iterator();
        }
    }

    @Override
    public long size() {
        return groupService.getAmountOfGroups();
    }

    @Override
    public IModel<CsldGroup> model(CsldGroup group) {
        return new Model<CsldGroup>(group);
    }
}
