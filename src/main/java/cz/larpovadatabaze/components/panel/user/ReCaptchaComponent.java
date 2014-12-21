package cz.larpovadatabaze.components.panel.user;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cz.larpovadatabaze.services.CsldUserService;

/**
 * User: Michal Kara Date: 21.12.14 Time: 7:03
 */
public class ReCaptchaComponent extends FormComponent<String> {

    private static final String FIELD_NAME="g-recaptcha-response";

    @SpringBean
    private CsldUserService userService;

    private class ReCaptchaValidator implements IValidator<String> {
        /**
         * Since convertInput() may be called multiple times for one response, we cache it to convert it just once
         */

        private String lastSuccessfulResponse;
        @Override
        public void validate(IValidatable<String> validatable) {
            boolean success = false;
            String value = validatable.getValue();
            if (value != null) {
                if (value.equals(lastSuccessfulResponse)) {
                    // Use cached value
                    success = true;
                }
                else {
                    /* Check captcha with google */
                    String ip = ((HttpServletRequest)getWebRequest().getContainerRequest()).getRemoteAddr();

                    try {
                        success = userService.checkReCaptcha(value, ip);
                    }
                    catch(CsldUserService.ReCaptchaTechnicalException e) {
                        ValidationError error = new ValidationError();
                        error.addKey("recaptcha.technical");
                        error(error);
                        return;
                    }
                }
            }

            if (!success) {
                // Add error
                ValidationError error = new ValidationError();
                error.addKey("recaptcha.failed");
                error(error);
                return;
            }
            else {
                // Remember this response
                lastSuccessfulResponse = value;
            }
        }
    }

    public ReCaptchaComponent(String id) {
        super(id);
    }

    public ReCaptchaComponent(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // We need to output markup id to refer to it in the render script
        setOutputMarkupId(true);

        // Add validator
        add(new ReCaptchaValidator());
    }

    @Override
    public String getInputName() {
        return FIELD_NAME;
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        // Include re-captcha JS
        response.render(JavaScriptHeaderItem.forUrl("https://www.google.com/recaptcha/api.js"));

        // Explicitly render re-captcha on load so it works when refreshing form via AJAX
        PackageTextTemplate tt = new PackageTextTemplate(getClass(), "ReCaptchaComponent_render.js");
        Map<String, String> args = new HashMap<String, String>();
        args.put("htmlElementId", getMarkupId());
        args.put("siteKey", userService.getReCaptchaSiteKey());
        response.render(OnDomReadyHeaderItem.forScript(tt.asString(args)));
    }
}
