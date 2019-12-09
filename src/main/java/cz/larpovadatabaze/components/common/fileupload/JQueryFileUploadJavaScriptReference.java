package cz.larpovadatabaze.components.common.fileupload;

import com.googlecode.wicket.jquery.ui.resource.JQueryUIResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * User: Michal Kara
 * Date: 24.12.13
 * Time: 23:29
 */
public class JQueryFileUploadJavaScriptReference extends JavaScriptResourceReference {

    private static final JQueryFileUploadJavaScriptReference INSTANCE = new JQueryFileUploadJavaScriptReference();

    public static JQueryFileUploadJavaScriptReference get() { return INSTANCE; }

    private JQueryFileUploadJavaScriptReference() {
        super(JQueryFileUploadJavaScriptReference.class, "jquery.fileupload.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        // We need JQuery UI loaded
        return Arrays.asList(JavaScriptHeaderItem.forReference(JQueryUIResourceReference.get()));
    }
}
