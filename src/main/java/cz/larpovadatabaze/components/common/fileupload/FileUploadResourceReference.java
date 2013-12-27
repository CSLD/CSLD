package cz.larpovadatabaze.components.common.fileupload;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.upload.FileItem;

import java.util.List;

/**
 * User: Michal Kara
 * Date: 24.12.13
 * Time: 22:16
 */
public class FileUploadResourceReference extends ResourceReference {

    private final long maxUploadSize;
    private final String paramName;
    private final IFileUploadCallback uploadCallback;

    /**
     * @see AbstractFileUploadResource
     */
    public FileUploadResourceReference(long maxUploadSize, String paramName, IFileUploadCallback uploadCallback) {
        super(FileUploadResourceReference.class, "file-upload");
        this.maxUploadSize = maxUploadSize;
        this.paramName = paramName;
        this.uploadCallback = uploadCallback;
    }

    @Override
    public IResource getResource() {
        return new AbstractFileUploadResource(maxUploadSize, paramName, uploadCallback)
        {
            @Override
            protected String generateJsonResponse(ResourceResponse resourceResponse, ServletWebRequest webRequest, List<FileItem> files) {
                JSONArray json = new JSONArray();

                for (FileItem fileItem : files)
                {
                    JSONObject fileJson = new JSONObject();

                    try {
                        fileJson.put("name", fileItem.getName());
//                        fileJson.put("url", getViewUrl(fileItem));
//                        fileJson.put("thumbnail_url", getViewUrl(fileItem));
                        fileJson.put("size", fileItem.getSize());
                        fileJson.put("delete_type", "POST");
//                        fileJson.put("delete_url", getDeleteUrl(fileItem));
                    } catch (JSONException e) {
                        try {
                            fileJson.put("error", e.getMessage());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    json.put(fileJson);
                }

                return json.toString();
            }
        };
    }
}
