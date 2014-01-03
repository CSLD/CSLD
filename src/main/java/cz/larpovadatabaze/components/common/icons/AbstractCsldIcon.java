package cz.larpovadatabaze.components.common.icons;

import cz.larpovadatabaze.api.Identifiable;
import cz.larpovadatabaze.services.IIconReferenceProvider;
import cz.larpovadatabaze.services.ImageService;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
        pp.add(ImageService.RESOURCE_REFERENCE_ID_PARAM_NAME, ((Identifiable) getDefaultModelObject()).getId());
        tag.put("src", urlFor(getService().getIconReference(), pp));
    }
}
