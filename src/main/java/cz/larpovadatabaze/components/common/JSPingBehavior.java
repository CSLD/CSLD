package cz.larpovadatabaze.components.common;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Pings back in specified intervals to keep the session alive
 *
 * User: Michal Kara Date: 12.12.14 Time: 19:54
 */
public class JSPingBehavior extends Behavior {
    /* Ping interval to keep session alive - 20 minutes */
    private static final int DEFAULT_PING_INTERVAL = 20*60;


    private final int interval;

    private final IResource resource;

    /**
     * @param interval Interval of ping (in seconds)
     */
    public JSPingBehavior(int interval) {
        this.interval = interval;
        this.resource = new ByteArrayResource("text/plain", "pong".getBytes());
    }

    /**
     * Construct with default interval
     */
    public JSPingBehavior() {
        this(DEFAULT_PING_INTERVAL);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        ResourceReference rr = new ResourceReference(getClass(), "pingPoint") {
            @Override
            public IResource getResource() {
                return resource;
            }
        };

        TextTemplate tt = new PackageTextTemplate(getClass(), "JSPingBehavior.js");
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("interval", Integer.toString(interval));
        variables.put("url", component.urlFor(rr, new PageParameters()).toString());
        response.render(new OnDomReadyHeaderItem(tt.asString(variables)));
    }
}
