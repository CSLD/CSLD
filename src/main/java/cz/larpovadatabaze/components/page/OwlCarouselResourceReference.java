package cz.larpovadatabaze.components.page;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * User: Michal Kara Date: 7.3.15 Time: 23:03
 */
public class OwlCarouselResourceReference extends JavaScriptResourceReference {
    protected  OwlCarouselResourceReference() {
        super(CsldBasePage.class, "css/bootstrap/plugins/owlcarousel/owl.carousel.min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Arrays.asList(
            new HeaderItem[] {
                JavaScriptHeaderItem.forReference(BootstrapResourceReference.get()), // We need bootstrap
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class, "css/bootstrap/plugins/owlcarousel/owl.carousel.css")),
                CssHeaderItem.forReference(new PackageResourceReference(CsldBasePage.class, "css/bootstrap/plugins/owlcarousel/owl.theme.css"))
            }
        );
    }

    // Singleton
    private final static OwlCarouselResourceReference singleton = new OwlCarouselResourceReference();
    public static OwlCarouselResourceReference get() { return singleton; }
}
