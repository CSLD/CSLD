/**
 * Handle clicks on more/less ccomments
 */
$('.jsMoreComments').click(function(e) {
    e.preventDefault();
    $('.jsMoreComments').hide();
    $('.jsLessComments').show();
    $('.jsVisibleComments').slideUp(200, function() {
        $('.jsHiddenComments').slideDown(400, function() {
            $('.jsComment').trigger('update.dot');
        });
    });
});

$('.jsLessComments').click(function(e) {
    e.preventDefault();
    $('.jsLessComments').hide();
    $('.jsMoreComments').show();
    $('.jsHiddenComments').slideUp(400, function() {
        $('.jsVisibleComments').slideDown(200, function() {
            $('.jsComment').trigger('update.dot');
        });
    });
    $('.jsComment').trigger('update.dot');
});
