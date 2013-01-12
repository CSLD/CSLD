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
                $(element).addClass("editable");
            }
        }

        function setAsNonEditable(element) {
            if ($(element).length > 0) {
                $(element)[0].contentEditable = false;
                $(element).css({
                    cursor: 'auto'
                });
                $(element).removeClass("editable");
            }
        }

        function addOneLabel(labels, elm, existingLabels){
            var actualLabel = $("<div></div>");
            actualLabel.html(elm);
            actualLabel.addClass("label");
            labels.append(actualLabel);
            if(existingLabels.indexOf(elm) != -1){
                actualLabel.addClass("checked");
            }
            actualLabel.bind("click", function(event){
                if($(this).hasClass("checked")){
                    $(this).removeClass("checked");
                } else {
                    $(this).addClass("checked");
                }
            });
        }

        function addEditable() {
            $(["#nazevHry", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
                "#days", "#description","#year", "#web"]).each(function (idx, element) {
                    setAsEditable(element);
                });
            var authors = [];
            var allAuthors = $("#authors").find("a");
            $("#authors").empty();
            allAuthors.each(function(idx, elm){
                $("#authors").append("<input class='author' value='"+$(elm).html()+"' />");
            });
            var emptyAuthor = "<input class='author' value='' />";
            $("#authors").append(emptyAuthor);
            var actButton = $("<input type='button' value='Další autor'/>");
            $("#authors").append(actButton);
            actButton.bind('click', function(evt){
                actButton.before(emptyAuthor);
            });

            $('img.obrazekHra').bind('click', function(){
                window.open("/editaceObrazku.jsp?gameId=" + $("#gameIdComment").val(), "_blank", "width=300,height=300,left=200,top=200")
            });

            $.ajax({
                url: '/ajax/getLabels.jsp',
                method: 'post',
                success: function(response){
                    response = JSON.parse(response.trim());
                    var existingLabels = [];
                    $("#labels").find("span").each(function(idx, element){
                        existingLabels.push($(element).html());
                    });
                    $("#labels").empty();

                    var labels = $("#labels");
                    labels.append("<div>Povinné:</div>");
                    $(response.required).each(function(idx, elm){
                        addOneLabel(labels, elm, existingLabels);
                    });
                    labels.append("<div style=\"clear:both;\" class=\"empty\"></div>");
                    $(response.labels).each(function(idx, elm){
                        addOneLabel(labels, elm, existingLabels);
                    });
                }
            });

            $.ajax({
                url: '/ajax/getAuthors.jsp',
                method: 'post',
                success: function(response){
                    response = JSON.parse(response.trim());
                    $(".author").autocomplete({
                        source: response.authors
                    });
                }
            });
        }

        function saveGameInfo() {
            var info;
            var labels = "";
            $("#labels").find("div.checked").each(function(idx, elm){
                labels += $(elm).html() + " / ";
            });
            if(labels.length > 0){
                labels = labels.substring(0, labels.length - 3);
            }

            var authors = "";
            $("#authors").find("input.author").each(function(idx, elm){
                if($(elm).val() != ""){
                    authors += $(elm).val() +", "
                }
            });
            if(authors.length > 0){
                authors = authors.substring(0, authors.length - 2);
            }

            $("#labels").empty();
            $("#labels").html(labels);
            $("#authors").empty();
            $("#authors").html(authors);
            $("img.obrazekHra").unbind();
            $.ajax({
                url:'/ajax/editGame.jsp',
                method: 'post',
                data:{
                    "nazevHry":$("#nazevHry").html(),
                    "labels": labels,
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
                    "imageSrc": $("img.obrazekHra").attr("src"),
                    "gameWeb": $("#web").html()
                },
                success:function (response) {
                    response = JSON.parse(response.trim());
                    if (response.status == "ok") {
                        alert("Hra byla úspěšně upravena");
                    } else {
                        alert("Hru se nepodařilo uložit. " + response.message);
                    }
                }
            });
        }

        function removeEditable() {
            $(["#nazevHry", "#menRole", "#womenRole", "#bothRole", "#playersAmount", "#hours",
                "#days", "#description","#year", "#web"]).each(function (idx, element) {
                    setAsNonEditable(element);
                });
        }

        if(role > 1){
            var edited = false;
            $('#editovatHru').bind('click', function (event) {
                edited = !edited;
                if (edited) {
                    $("#editovatHru").val('Uložit');
                    addEditable();
                } else {
                    $("#editovatHru").val('Editovat');
                    saveGameInfo();
                    removeEditable();
                }
                return false;
            });
        }
    }
});