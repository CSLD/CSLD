package cz.larpovadatabaze.providers;

import cz.larpovadatabaze.entities.CsldUser;
import cz.larpovadatabaze.services.CsldUserService;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 28.4.13
 * Time: 18:51
 */
public class SortableUserProvider extends SortableDataProvider<CsldUser, String> {
    private CsldUserService csldUserService;
    public SortableUserProvider(CsldUserService csldUserService){
        this.csldUserService = csldUserService;
        setSort("name", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends CsldUser> iterator(long first, long count) {
        SortParam<String> props = getSort();
        if(props.getProperty().equals("name")) {
            return csldUserService.getOrderedByName().iterator();
        } else if(props.getProperty().equals("comments")) {
            return csldUserService.getOrderedByComments().iterator();
        } else {
            return csldUserService.getOrderedByPlayed().iterator();
        }
    }

    @Override
    public long size() {
        return csldUserService.getAll().size();
    }

    @Override
    public IModel<CsldUser> model(CsldUser csldUser) {
        return new Model<CsldUser>(csldUser);
    }
}
