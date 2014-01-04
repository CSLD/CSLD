package cz.larpovadatabaze.components.common.social;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.template.PackageTextTemplate;

/**
 * Buttons to share page on social networks
 *
 * User: Michal Kara
 * Date: 4.1.14
 * Time: 9:57
 */
public class SocialShareButtons extends Panel {

    PackageTextTemplate initJS;
    WebMarkupContainer fbButton;
    WebMarkupContainer gpButton;
    WebMarkupContainer twButton;

    public SocialShareButtons(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Determine url of the page
        WebPage p = getWebPage();
        String url = RequestCycle.get().getUrlRenderer().renderFullUrl(Url.parse(p.urlFor(p.getPageClass(), p.getPageParameters()).toString()));

        // Produce public URL when running on localhost - for testing, may be eventually commented out
        final String LOCAL_PREFIX = "http://localhost:8084/";
        final String PROD_PREFIX = "http://www.larpovadatabaze.cz/";
        if (url.startsWith(LOCAL_PREFIX)) {
            url = PROD_PREFIX+url.substring(LOCAL_PREFIX.length());
        }

        /* Add components */

        // Initializing JS
        initJS = new PackageTextTemplate(this.getClass(), "SocialShareButtonsInit.js");

        // FB button
        fbButton = new WebMarkupContainer("fbButton");
        fbButton.add(new AttributeModifier("data-href", url));
        add(fbButton);

        // G+ button
        gpButton = new WebMarkupContainer("gpButton");
        gpButton.add(new AttributeModifier("data-href", url));
        add(gpButton);

        // Twitter button
        twButton = new WebMarkupContainer("twButton");
        twButton.add(new AttributeModifier("data-href", url));
        add(twButton);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Render needed JS
        response.render(OnDomReadyHeaderItem.forScript(initJS.getString()));
    }
}
