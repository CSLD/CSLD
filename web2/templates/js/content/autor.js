var loggedUser, role;
$(document).ready(function () {
    function setAsEditable(element) {
        if ($(element).length > 0) {
            $(element)[0].contentEditable = true;
        }
    }

    function setAsNonEditable(element) {
        if ($(element).length > 0) {
            $(element)[0].contentEditable = false;
        }
    }

    function addEditable() {
        $(["#nazevHry", "#labels", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
            "#days", "#authors", "#description","#year"]).each(function (idx, element) {
                setAsEditable(element);
            });
    }

    function removeEditable() {
        $(["#nazevHry", "#labels", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
            "#days", "#authors", "#description","#year"]).each(function (idx, element) {
                setAsNonEditable(element);
            });
    }

    $('#editovatAutora').bind("click", function(){
        $('#possibleUsers').slideToggle();
        if($("#editovatAutora").html() == '<b>Editovat</b>'){
            $("#editovatAutora").html("<b>Uložit</b>");
            addEditable();
        } else {
            $("#editovatAutora").html("<b>Editovat</b>");
            removeEditable();
        }
    });

    $(".user").bind("click", function(){
        var userId = $(this).find("span.hidden").html();
        var authorId = $("#autorId").html();
        $.ajax({
            url: '/ajax/editAuthor.jsp',
            method: 'POST',
            data: {
                authorId: authorId,
                userId: userId
            },
            success: function(response){
                response = JSON.parse(response.trim());
                if(response.status == "ok"){
                    alert("Autor byl v pořádku napárován.");
                    location.reload();
                } else {
                    alert("Nepovedlo se autora upravit" + response.message);
                }
            }
        });

    });
});