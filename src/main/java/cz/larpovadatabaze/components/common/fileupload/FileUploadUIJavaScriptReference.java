package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Arrays;

/**
 * Reference to file upload UI javascript
 *
 * User: Michal Kara
 * Date: 25.12.13
 * Time: 9:32
 */
public class FileUploadUIJavaScriptReference extends JavaScriptResourceReference {

    private static final FileUploadUIJavaScriptReference INSTANCE = new FileUploadUIJavaScriptReference();

    public static FileUploadUIJavaScriptReference get() { return INSTANCE; }

    public FileUploadUIJavaScriptReference() {
        super(FileUploadUIJavaScriptReference.class, "fileUploadUI.js");
    }

    @Override
    public Iterable<? extends HeaderItem> getDependencies() {
        return Arrays.asList(JavaScriptReferenceHeaderItem.forReference(JQueryFileUploadJavaScriptReference.get()));
    }
}
