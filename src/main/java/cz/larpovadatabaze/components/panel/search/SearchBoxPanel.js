/**
 * JS to handle search-on-type
 *
 * User: Michal
 * Date: 19.7.15
 * Time: 21:42
 */
(function() {
    "use strict";

    var timer = null;

    var clearTimer = function() {
        if (timer != null) {
            window.clearTimeout(timer);
        }
        timer = null;
    };

    var installTimer = function() {
        clearTimer();
        timer = window.setTimeout(function() {
            // Click to hidden search link after timeout if there is something in the input
            timer = null;
            if ($('#${textInputId}').val().length > parseInt('${minTermLength}')) {
                $('#${searchLinkId}').click();
            }
        }, parseInt('${timeoutMS}'));
    };

    // Handlers for input
    $('#${textInputId}').keyup(installTimer).blur(function() {
        clearTimer();
        //$('#${resultWrapperId}').hide();
    });

    // Stop timer when Wicket AJAX call is started
    Wicket.Event.subscribe('/ajax/call/before', clearTimer);
})();
