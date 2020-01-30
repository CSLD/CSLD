package cz.larpovadatabaze.common.components.icons;

import cz.larpovadatabaze.common.Identifiable;
import cz.larpovadatabaze.common.services.IIconReferenceProvider;
import cz.larpovadatabaze.games.services.Images;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;
import java.util.Date;

/**
 * User icon, needs IMG tag,
 *
 * User: Michal Kara
 * Date: 3.1.14
 * Time: 14:33
 */
public abstract class AbstractCsldIcon<T extends IIconReferenceProvider<O>, O extends Identifiable> extends WebComponent {
    abstract protected T getService();

    public AbstractCsldIcon(String id, IModel<O> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (!"img".equalsIgnoreCase(tag.getName())) {
            throw new IllegalArgumentException("Icon component must be an <img /> tag");
        }

        PageParameters pp = new PageParameters();
        Serializable id = ((Identifiable) getDefaultModelObject()).getId();
        pp.add(Images.RESOURCE_REFERENCE_ID_PARAM_NAME,
                id != null ? id : "");
        tag.put("src", urlFor(getService().getIconReference(), pp) + "&imageId=" + new Date().getTime());
    }
}
