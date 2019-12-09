package cz.larpovadatabaze.components.page;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * Resource reference for CSLD css
 *
 * User: Michal Kara Date: 7.3.15 Time: 23:16
 */
public class CsldCssResourceReference extends CssResourceReference {
    protected CsldCssResourceReference() {
        super(CsldBasePage.class,"css/devel.css");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Arrays.asList(
            new HeaderItem[] {
                JavaScriptHeaderItem.forReference(BootstrapResourceReference.get()), // We need bootstrap
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/smoothness/jquery-ui-1.8.24.custom.css")),
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/style.css")),
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/custom.css")),
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class,"css/hint.css"))
            }
        );
    }

    // Singleton
    private final static CsldCssResourceReference singleton = new CsldCssResourceReference();
    public static CsldCssResourceReference get() { return singleton; }
}
