/**
 * Javascript for larp.cz calendar panel
 */
jQuery.getJSON('http://files.korh.cz/larp.cz-kalendar-api/kalendar.php', function (data) {
    var MAX_ITEMS = 6;
    var ITEMS_PER_ROW = 2;

    if (data.chyba) { // Check alert
        console.error(data.chyba.popis);
        return;
    }
    else if (!data.larpy) {
        console.error("Data have no 'larpy' variable!");
        return''
    }

    /* Seems OK */

    // Get container
    var container = $('.jsUpcomingEvents');

    // Get template
    var template = container.find('.jsUpcomingEventTemplate');

    // Loop
    var remaining = MAX_ITEMS; // How many items are remaining
    var rowRemainingItems = 0; // How many elements can we put to a row
    var row = null; // Row we add to; will be filled later
    for(var i=0; i<data.larpy.length; i++) {
        // Check if we should end
        if (remaining-- <= 0) {
            break;
        }

        // Create row if needed
        if (rowRemainingItems < 1) {
            row = $('<div class="row recent-games"></div>');
            row.appendTo(container);
            rowRemainingItems = ITEMS_PER_ROW;
        }

        /* Add item */

        // Get larp
        var larp = data.larpy[i];

        // Clone new item
        var item = template.clone();

        // Fill in item
        item.find('.jsUELinkName').attr('href', larp.link).text(larp.nazev);
        item.find('.jsUEDate').text(larp.datum);
        item.find('.jsUEPlayers').text(larp.pocet);
        item.find('.jsUEPlace').text(larp.obec + ', ' + larp.kraj);

        // Add to row
        item.appendTo(row);
        rowRemainingItems--;
    }

    // Remove template from DOM
    template.remove();
});
