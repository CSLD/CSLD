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
 * It returns only users meaning that those with isAuthor = true are not to be seen here.
 */
public class SortableUserProvider extends SortableDataProvider<CsldUser, String> {
    private CsldUserService csldUserService;
    public SortableUserProvider(CsldUserService csldUserService){
        this.csldUserService = csldUserService;
        setSort("comments", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends CsldUser> iterator(long first, long amountPerPage) {
        SortParam<String> props = getSort();
        if(props.getProperty().equals("name")) {
            return csldUserService.getOrderedUsersByName(first, amountPerPage).iterator();
        } else if(props.getProperty().equals("comments")) {
            return csldUserService.getOrderedUsersByComments(first, amountPerPage).iterator();
        } else {
            return csldUserService.getOrderedUsersByPlayed(first, amountPerPage).iterator();
        }
    }

    @Override
    public long size() {
        return csldUserService.getAmountOfAuthors();
    }

    @Override
    public IModel<CsldUser> model(CsldUser csldUser) {
        return new Model<CsldUser>(csldUser);
    }
}
