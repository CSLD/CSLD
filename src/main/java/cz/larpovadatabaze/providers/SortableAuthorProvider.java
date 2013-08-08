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
 * Time: 16:18
 */
public class SortableAuthorProvider extends SortableDataProvider<CsldUser, String> {
    private CsldUserService csldUserService;
    public SortableAuthorProvider(CsldUserService csldUserService){
        this.csldUserService = csldUserService;
        setSort("form.wholeName", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends CsldUser> iterator(long first, long last) {
        SortParam<String> sortings = getSort();
        String property = sortings.getProperty();
        int amountOfAuthors = csldUserService.getAuthorsByGames().size();
        if(amountOfAuthors > last) {
            last = amountOfAuthors;
        }
        if(property.equals("form.wholeName")){
            return csldUserService.getAuthorsByGames().subList((int)first,(int)last).iterator();
        } else {
            return csldUserService.getAuthorsByBestGame().subList((int)first,(int)last).iterator();
        }
    }

    @Override
    public long size() {
        return csldUserService.getAuthorsByGames().size();
    }

    @Override
    public IModel<CsldUser> model(CsldUser csldUser) {
        return new Model<CsldUser>(csldUser);
    }
}
