package cz.larpovadatabaze.common.components;

import cz.larpovadatabaze.common.components.page.CsldBasePage;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * User: Michal Kara Date: 7.3.15 Time: 23:12
 */
public class BootstrapResourceReference extends JavaScriptResourceReference {
    protected BootstrapResourceReference() {
        super(CsldBasePage.class,"css/bootstrap/js/bootstrap.min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Arrays.asList(
            new HeaderItem[]{
                JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()), // Bootstrap needs jQuery
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class, "css/bootstrap/css/bootstrap.css"))
            }
        );
    }

    // Singleton
    private static final BootstrapResourceReference singleton = new BootstrapResourceReference();
    public static BootstrapResourceReference get() { return singleton; }
}
