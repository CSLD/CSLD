var loggedUser, role;

function disableCtrlKeyCombination(event){
    var keyCode = event.keyCode;
    if (event.ctrlKey && keyCode==86) { //CTRL+V
        event.preventDefault();
        $(event.target).text(system.getClipboard().getData("text"));
        return false;
    } else if (event.ctrlKey && keyCode==67) { //CTRL+C (Copy)
        event.preventDefault();
        system.getClipboard().setData("text",$(event.target).text());
        return false;
    } else {
        return true;
    }
}

var browser;
if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
    //var ffversion=new Number(RegExp.$1) // capture x.x portion and store as a number
    browser = "FF";
}
else if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) {
    //var ieversion=new Number(RegExp.$1) // capture x.x portion and store as a number
    browser = "IE";
}
else if (/Opera[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
    //var oprversion=new Number(RegExp.$1) // capture x.x portion and store as a number
    browser = "OPERA";
}
else if(/Chrome/.test(navigator.userAgent)){
    browser = "Chrome";
}
else {
    browser = "Unknown";
}
if(browser == "FF"){
    $(document).keypress(disableCtrlKeyCombination);
}

$(document).ready(function () {
    if (loggedUser) {
        $(".played").bind("click", function (event) {
            if (loggedUser) {
                $("#ucastFormular").submit();
            } else {
                return false;
            }
        });

        $(".pridatKomentarButton").bind("click", function (event) {
            if (loggedUser) {
                $("#komentarFormular").submit();
            } else {
                return false;
            }
        });

        $("#tlacitkoHodnotit").bind("click", function (event) {
            if (loggedUser) {
                $("#hodnoceniFormular").submit();
            } else {
                return false;
            }
        });

        function setAsEditable(element) {
            if ($(element).length > 0) {
                $(element)[0].contentEditable = true;
                $(element).css({
                    cursor: 'pointer'
                });
            }
        }

        function setAsNonEditable(element) {
            if ($(element).length > 0) {
                $(element)[0].contentEditable = false;
                $(element).css({
                    cursor: 'auto'
                });
            }
        }

        function addEditable() {
            $(["#nazevHry", "#labels", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
                "#days", "#authors", "#description","#year"]).each(function (idx, element) {
                    setAsEditable(element);
                });
            if($('#labels').height() < 18){
                $('#labels').css({
                    height: 18,
                    width: "100%"
                });
            }
            if($('#authors').height() < 18){
                $('#authors').css({
                    height: 18,
                    width: "100%"
                });
            }
            $.ajax({
                url: '/ajax/getLabels.jsp',
                method: 'post',
                success: function(response){
                    response = JSON.parse(response.trim());
                    var stringLabels = "";
                    var requiredLabels = "";
                    $(response.required).each(function(idx, elm){
                        requiredLabels += elm + ", "
                    });
                    if(response.required.length > 0){
                        requiredLabels = requiredLabels.substring(0, requiredLabels.length - 2);
                    }
                    $(response.labels).each(function(idx, elm){
                        stringLabels += elm + ", "
                    });
                    if(response.labels.length > 0){
                        stringLabels = stringLabels.substring(0, stringLabels.length - 2);
                    }
                    $('#game').prepend("<div id='allLabels' style='width: 80%'>" +
                        "<div id='required'><b>Požadovaný: </b>" + requiredLabels + "</div>" +
                        "<div id='optional'><b>Volitelné: </b>" + stringLabels + "</div></div>");
                }
            });
        }

        function saveGameInfo() {
            var info;
            var authors = $("#authors").text().trim();
            $.ajax({
                url:'/ajax/editGame.jsp',
                method: 'post',
                data:{
                    "nazevHry":$("#nazevHry").html(),
                    "labels":$("#labels").html(),
                    "menRole":$("#menRole").html(),
                    "womenRole":$("#womenRole").html(),
                    "bothRole":$("#bothRole").html(),
                    "playersAmount":$("#playersAmount").html(),
                    "hours":$("#hours").html(),
                    "days":$("#days").html(),
                    "year":$("#year").html(),
                    "authors":authors,
                    "description":$("#description").html(),
                    "gameId": $("#gameIdComment").val(),
                    "imageSrc": $("img.obrazekHra").attr("src")
                },
                success:function (response) {
                    response = JSON.parse(response.trim());
                    console.log(response);
                    if (response.status == "ok") {
                        alert("Hra byla úspěšně upravena");
                    } else {
                        alert("Hru se nepodařilo uložit. " + response.message);
                    }
                }
            });
        }

        function removeEditable() {
            $(["#nazevHry", "#labels", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
                "#days", "#authors", "#description","#year"]).each(function (idx, element) {
                    setAsNonEditable(element);
                });

            $("#authors").unbind('focus');
            $("#labels").unbind('focus');
            $("#allLabels").remove();
        }

        if(role > 1){
            var edited = false;
            $('#editovatHru').bind('click', function (event) {
                edited = !edited;
                if (edited) {
                    $("#editovatHru").html('<b>Uložit</b>');
                    addEditable();
                } else {
                    $("#editovatHru").html('<b>Editovat</b>');
                    saveGameInfo();
                    removeEditable();
                }
                return false;
            });
        }
    }
});