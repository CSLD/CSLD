/**
 * Javascript part of JSPingBehavior
 */
var _ = function () {
    "use strict";

    var timerId = window.setInterval(function() {
        if (window.console) window.console.log("Doing ping");
        $.ajax({
            url: "${url}",
            dataType: "text",
            type: "POST",
            success: function(data) {
                if (window.console) window.console.log("Got '"+data+"'");
                if (data != 'pong') {
                    // Remove timer
                    if (window.console) window.console.log("Removing timeout");
                    window.clearTimeout(timerId);
                }
            }
        });
    }, eval("${interval}")*1000);
}();
