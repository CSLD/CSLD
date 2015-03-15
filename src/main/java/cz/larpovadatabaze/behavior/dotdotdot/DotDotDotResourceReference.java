package cz.larpovadatabaze.behavior.dotdotdot;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Arrays;

/**
 * Resource reference for DotDotDot component
 *
 * User: Michal Kara Date: 15.3.15 Time: 16:49
 */
public class DotDotDotResourceReference extends PackageResourceReference {
    protected DotDotDotResourceReference() {
        super(DotDotDotResourceReference.class, "jquery.dotdotdot.min.js");
    }

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        // We need jQuery
        return Arrays.asList(new HeaderItem[] {JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())});
    }

    private static final DotDotDotResourceReference singleton = new DotDotDotResourceReference();
    public static DotDotDotResourceReference get() { return singleton; }
}
