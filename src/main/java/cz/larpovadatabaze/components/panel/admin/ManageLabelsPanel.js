$('.startEdit').click(function() {
    var id = $(this).attr('data-id');
    var text = $(this).parents('td').find('span').text();
    $('#editedLabelId').val(id);
    var d = $('#editDialog');
    d.css('left', (window.innerWidth-d.outerWidth())/2+'px');
    d.css('top', (window.innerHeight-d.outerHeight())/2+'px');
    d.show();
    $('#newLabelDescription').val(text).focus();
});

$('#editDialog').find('input[type=button]').click(function() {
    $('#editedLabelId').val('');
    $('#editDialog').hide();
});
