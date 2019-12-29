package cz.larpovadatabaze.common.components.gallery;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * Reference to file upload UI javascript
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 9:32
 */
public class GalleryJavaScriptReference extends JavaScriptResourceReference {

    private static final GalleryJavaScriptReference INSTANCE = new GalleryJavaScriptReference();

    public static GalleryJavaScriptReference get() { return INSTANCE; }

    public GalleryJavaScriptReference() {
        super(GalleryJavaScriptReference.class, "gallery.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        if (Application.exists()) {
            // Get from application config
            return Arrays.asList(JavaScriptReferenceHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        }
        else {
            // Use default
            return Arrays.asList(JavaScriptReferenceHeaderItem.forReference(JQueryResourceReference.get()));
        }
    }
}
