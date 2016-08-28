/* grecaptcha takes some time to load, so wait until it is available */
var _ = function () {
    "use strict";

    function renderReCaptcha() {
        if (typeof grecaptcha == "object") {
            // Stop timer
            window.clearTimeout(timer);

            grecaptcha.render('${htmlElementId}', {
                'sitekey' : '${siteKey}'
            });
        }
    }

    var timer = window.setTimeout(renderReCaptcha, 100);
}();