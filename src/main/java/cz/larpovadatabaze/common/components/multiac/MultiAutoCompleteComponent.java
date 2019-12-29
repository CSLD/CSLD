package cz.larpovadatabaze.common.components.multiac;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONException;
import com.github.openjson.JSONObject;
import cz.larpovadatabaze.common.api.Identifiable;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.template.PackageTextTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Michal Kara Date: 27.6.15 Time: 14:58
 */
public class MultiAutoCompleteComponent<T extends Identifiable & IAutoCompletable> extends FormComponent<List<T>> {
    private final static Logger logger = Logger.getLogger(MultiAutoCompleteComponent.class);
    public static final String QUERY_PARAM = "query";

    /**
     * Source for choices
     */
    private final IMultiAutoCompleteSource<T> source;

    private DataSourceBehavior sourceBehavior;

    public MultiAutoCompleteComponent(String id, IModel<List<T>> model, IMultiAutoCompleteSource<T> source) {
        super(id, model);
        this.source = source;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Ensure we render markup id
        setOutputMarkupId(true);

        // Add behavior
        sourceBehavior = new DataSourceBehavior();
        add(sourceBehavior);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (!"input".equalsIgnoreCase(tag.getName())) {
            throw new IllegalArgumentException("MultiAutoCompleteComponent must be placed on input tag");
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Add JS dependency
        response.render(JavaScriptHeaderItem.forReference(MagicSuggestJavaScriptReference.get()));

        // Add CSS dependency
        response.render(CssHeaderItem.forReference(MagicSuggestCssReference.get()));

        // Render initialization js
        try(PackageTextTemplate ptt = new PackageTextTemplate(getClass(), "MultiAutoCompleteComponent_init.js")) {
            Map<String, String> args = new HashMap<String, String>();
            args.put("componentId", getMarkupId());
            args.put("dataUrl", sourceBehavior.getCallbackUrl().toString());

            try {
                // Set initial values
                JSONArray values = new JSONArray();

                if (getModelObject() != null) {
                    for (T v : getModelObject()) {
                        JSONObject value = new JSONObject();
                        value.put("id", v.getId());
                        value.put("name", v.getAutoCompleteData());
                        values.put(value);
                    }
                }

                args.put("value", values.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            response.render(OnDomReadyHeaderItem.forScript(ptt.asString(args)));
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private class DataSourceBehavior extends AbstractDefaultAjaxBehavior {

        @Override
        protected void respond(AjaxRequestTarget target) {
            final RequestCycle requestCycle = RequestCycle.get();
            final String value = requestCycle.getRequest().getPostParameters().getParameterValue(QUERY_PARAM).toString();

            final IRequestHandler handler = newRequestHandler(value);
            requestCycle.scheduleRequestHandlerAfterCurrent(handler);
        }

        private IRequestHandler newRequestHandler(final String input)
        {
            return new IRequestHandler()
            {
                @Override
                public void respond(final IRequestCycle requestCycle)
                {
                    WebResponse response = (WebResponse)requestCycle.getResponse();

                    final String encoding = Application.get().getRequestCycleSettings().getResponseRequestEncoding();
                    response.setContentType("text/json; charset=" + encoding);
                    response.disableCaching();

                    JSONArray arr = new JSONArray();

                    if (StringUtils.isNotEmpty(input)) {
                        try {
                            for(T val : source.getChoices(input)) {
                                JSONObject choice = new JSONObject();
                                choice.put("id", val.getId());
                                choice.put("name", val.getAutoCompleteData());

                                arr.put(choice);
                            }
                        }
                        catch(JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    response.write(arr.toString());
                }

                @Override
                public void detach(IRequestCycle requestCycle) {
                }
            };
        }
    }

    @Override
    public String[] getInputAsArray() {
        List<StringValue> params = RequestCycle.get().getRequest().getPostParameters().getParameterValues(getInputName()+"[]");
        if (params == null) {
            return null;
        }

        String[] res = new String[params.size()];
        int i = 0;
        for(StringValue v : params) {
            res[i++] = v.toString();
        }
        return res;
    }

    @Override
    protected List<T> convertValue(String[] value) throws ConversionException {
        if (value == null) {
            return null;
        }

        List<T> res = new ArrayList<T>();
        for(String id : value) {
            res.add(source.getObjectById(Long.parseLong(id)));
        }
        return res;
    }
}
