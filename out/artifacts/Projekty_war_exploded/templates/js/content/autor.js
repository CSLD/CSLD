var loggedUser, role;
$(document).ready(function () {
    $('#editovatAutora').bind("click", function(){
        $('possibleUsers').slideToggle();
    });

    $(".user").bind("click", function(){
        $(this);
    });
});