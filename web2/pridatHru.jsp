<%@ page import="org.pilirion.models.game.Labels" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.Label" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.models.user.Groups" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- GAME -->
    <!-- LEVA CAST -->
    <script>
        $(function() {
            var availableTags = [
                    <%
                       Authors authors = new Authors(conn);
                       out.println(authors.getNamesAsArray());
                    %>
            ];

            var availableGroups = [
                    <%
                        Groups groups = new Groups(conn);
                        out.println(groups.getNamesAsArray());
                    %>
            ];
            $("#skupina_autora").autocomplete({
                source: availableGroups
            });
            $( "#autor1" ).autocomplete({
                source: availableTags
            });
            $( "input[name=autor2]" ).autocomplete({
                source: availableTags
            });
            $( "input[name=autor3]" ).autocomplete({
                source: availableTags
            });
            $( "input[name=autor4]" ).autocomplete({
                source: availableTags
            });
            $( "input[name=autor5]" ).autocomplete({
                source: availableTags
            });
        });

        $(document).ready(function(){
            function writeError(element){
                removeError(element);
                $(element).parent().append("<span class='error' style='color:red'>Pole je povinné.</span>");
            }

            function removeError(element){
                $(element).parent().find("span.error").remove();
            }

            $(".pridatAutoraButton").bind("click", function(){
                var isCorrect = true;
                if($("#jmeno_autora").val() == "" || $("#jmeno_autora").val() == "Jméno"){
                    writeError($("#jmeno_autora"));
                    isCorrect = false;
                } else {
                    removeError($("#jmeno_autora"));
                }
                if($("#prijmeni_autora").val() == "" || $("#prijmeni_autora").val() == "Příjmení") {
                    writeError($("#prijmeni_autora"));
                    isCorrect = false;
                } else {
                    removeError($("#prijmeni_autora"));
                }
                return isCorrect;
            });

            $(".pridatStitekButton").bind("click", function(){
                var isCorrect = true;
                if($("#nazev_stitku").val() == "" || $("#nazev_stitku").val() == "Název štítku"){
                    writeError($("#nazev_stitku"));
                    isCorrect = false;
                } else {
                    removeError($("#nazev_stitku"));
                }
                if($("#popis_stitku").val() == "" || $("#popis_stitku").val() == "Popis štítku") {
                    writeError($("#popis_stitku"));
                    isCorrect = false;
                } else {
                    removeError($("#popis_stitku"));
                }
                return isCorrect;
            });

            $(".pridatHruButton").bind("click", function(){
                var isCorrect = true;
                if($("#nazev").val() == "" || $("#nazev").val() == "Název"){
                    writeError($("#nazev"));
                    isCorrect = false;
                } else {
                    removeError($("#nazev"));
                }
                if($("#autor1").val() == "" || $("#autor1").val() == "Autor"){
                    writeError($("#autor1"));
                    isCorrect = false;
                } else {
                    removeError($("#autor1"));
                }
                var isChecked = false;
                $(".required").each(function(idx, element){
                    if($(element).is(':checked')){
                        isChecked = true;
                    }
                });
                return isChecked && isCorrect;
            });

            $("#popis").bind('click', function(){
                if($(this).html() == "Zde zadejte popis hry"){
                    $(this).html("");
                }
            });
        });


    </script>


    <div id="levaCastPridatHru">
        <form id="PridatHru" ENCTYPE="multipart/form-data" action="handlers/pridatHru.jsp" method="post">
            <div class="sekce" style="margin-top: 0px;">
                <div class="nadpisStranky"><img src="img/icon/plus_icon18.png" class="ikonaStranky">Přidat hru</div>
                <table>
                    <%
                        String message = request.getParameter("message");
                        String error = request.getParameter("error");
                        if(message != null){
                    %>
                    <tr>
                        <td style="color: green;" colspan="2" class="popis"><%=message%></td>
                    </tr>
                    <%
                        } if(error != null){
                    %>
                    <tr>
                        <td style="color: red;" colspan="2" class="popis"><%=error%></td>
                    </tr>
                    <%
                        }
                    %>
                    <tr>
                        <td><input type="file" name="logohry" id="uploadfile" style="margin: 0px;"/></td>
                        <td class="popis">Vyberte logo hry o rozměrech 120x120px s maximální velikostí 2MB</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="nazev" id="nazev" title="Název" value=""></td>
                        <td class="popis">Zadejte název hry</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="autor1" id="autor1" title="Autor" value="" > <a href="/" onclick="$('#autor2').slideToggle('slow'); return false;" class="pridatAutora">(další autor)</a></td>
                        <td class="popis">Vyberte ze seznamu autora hry (zobrazí se po zadání prvního písmena)</td>
                    </tr>
                    <div class="viceAutoru">
                        <tr>
                            <td>
                                <div id="autor2"><input class="textbox" type="text" name="autor2" title="Autor" value="" ><a href="/" onclick="$('#autor3').slideToggle('slow'); return false;" class="pridatAutora"> (další autor)</a></div>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <div id="autor3"><input class="textbox" type="text" name="autor3" title="Autor" value="" ><a href="/" onclick="$('#autor4').slideToggle('slow'); return false;" class="pridatAutora"> (další autor)</a></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div id="autor4"><input class="textbox" type="text" name="autor4" title="Autor" value="" ><a href="/" onclick="$('#autor5').slideToggle('slow'); return false;" class="pridatAutora"> (další autor)</a></div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div id="autor5"><input class="textbox" type="text" name="autor5" title="Autor" value="" ></div>
                            </td>
                        </tr>
                    </div>
                    <tr>
                        <td><a href="/" onclick="$('#pridatAutoraForm').slideToggle('slow'); return false;" class="pridatAutora">(přidat autora)</a></td>
                        <td class="popis">Přidejte autora do seznamu autorů</td>
                    </tr>
                    <tr>
                        <td>
                            <div id="pridatAutoraForm">
                                <table>
                                    <tr>
                                        <td>
                                            <input class="textbox" type="text" name="jmeno_autora" id="jmeno_autora"
                                                   title="Jméno" value="">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <input class="textbox" type="text" name="prijmeni_autora"
                                                   id="prijmeni_autora" title="Příjmení" value="">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <input class="textbox" type="text" name="prezdivka_autora"
                                                   id="prezdivka_autora" title="Přezdívka" value="">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <input class="textbox" type="text" name="skupina_autora"
                                                   id="skupina_autora" title="Skupina" value="">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <button class="pridatAutoraButton" type="submit" name="pridat_autora"
                                                    title="Přidat autora" formaction="/handlers/pridatAutora.jsp"
                                                    formenctype="application/x-www-form-urlencoded"
                                                    formmethod="post">Přidat
                                            </button>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="rok_vytvoreni" id="rok_vytvoreni" title="Rok vytvoření"
                                   value=""></td>
                        <td class="popis">Zadejte rok vytvoření hry</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="pocet_hracu" id="pocet_hracu" title="Počet hráčů"
                                   value=""></td>
                        <td class="popis">Zadejte alespoň přibližný počet hráčů</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="pocet_muzu" id="pocet_muzu" title="Muži" value=""
                                   style="width: 38px;">
                            <input class="textbox" type="text" name="pocet_zen" id="pocet_zen" title="Ženy" value=""
                                   style="width: 38px;">
                            <input class="textbox" type="text" name="pocet_obojetne" id="pocet_obojetne" title="Obojetné" value=""
                               style="width: 37px;"></td>
                        <td class="popis">Pokud je to pro hru podstatné, zadejte počet mužů, žen, případně obojetných</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="hodiny" id="hodiny" title="Hodiny" value=""
                                   style="width: 60px;">
                            <input class="textbox" type="text" name="dny" id="dny" title="Dny" value=""
                                   style="width: 60px;"></td>
                        <td class="popis">Zadejte alespoň přibližnou délku celé hry</td>
                    </tr>
                    <tr>
                        <td class="stitky popis" colspan="2">Označte štítky, které vystihují Váš larp. Pokud nespadá
                            jednoznačně pod jednu kategorii, vyberte jich více.
                        </td>
                    </tr>
                </table>


                <fieldset class="fieldset">
                    <ul class="checklist">
                        <i>povinné</i>
                        <%
                            Labels labels = new Labels(conn);
                            List<Label> requiredLabels = labels.getRequiredLabels();
                            List<Label> otherLabels = labels.getOtherLabels();
                            for (Label label : requiredLabels) {
                        %>
                        <li>
                            <input class="required" name="<%=label.getName()%>" value="<%=label.getName()%>" type="checkbox"
                                   id="<%=label.getName()%>"/>
                            <a class="checkbox-select stitekNapoveda" href="#"><%=label.getName()%>
                                <span class="popisekNapoveda"><%=label.getDescription()%></span></a>
                            <a class="checkbox-deselect stitekNapoveda" href="#"><%=label.getName()%>
                                <span class="popisekNapoveda"><%=label.getDescription()%></span></a>
                        </li>
                        <%
                            }
                        %>
                        <div style="height:12px; "></div>
                        <%
                            for (Label label : otherLabels) {
                                if(!label.isAuthorized() && loggedUser.getId() != label.getUserId()){
                                    continue;
                                }
                        %>
                        <li>
                            <input name="<%=label.getName()%>" value="<%=label.getName()%>" type="checkbox"
                                   id="<%=label.getName()%>"/>
                            <a class="checkbox-select stitekNapoveda" href="#"><%=label.getName()%>
                                <span class="popisekNapoveda"><%=label.getDescription()%></span></a>
                            <a class="checkbox-deselect stitekNapoveda" href="#"><%=label.getName()%>
                                <span class="popisekNapoveda"><%=label.getDescription()%></span></a>
                        </li>
                        <%
                            }
                        %>
                    </ul>
                </fieldset>
                <div class="pridatStitek"><a href="\"
                                             onclick="$('#pridatStitekForm').slideToggle('slow'); return false;">(přidat
                    štítek)</a></div>
                <div id="pridatStitekForm">
                        <table>
                        <tr>
                            <td>
                                <input class="textbox" type="text" name="nazev_stitku" id="nazev_stitku"
                                       title="Název štítku" value="">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input class="textbox" type="text" name="popis_stitku" id="popis_stitku"
                                       title="Popis štítku" value="">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <button class="pridatStitekButton" type="submit" name="pridat_stitek"
                                        title="Přidat štítek"  formaction="/handlers/pridatStitek.jsp"
                                        formenctype="application/x-www-form-urlencoded"
                                        formmethod="post">Přidat
                                </button>
                            </td>
                        </tr>
                    </table>
                </div>


                <textarea class="textarea" name="popis" id="popis" title="Popis hry" value=""
                          style="width: 90%; height: 100%" rows="5">Zde zadejte popis hry</textarea>
                <button class="pridatHruButton" type="submit" name="pridat_hru" title="Přidat hru"
                        >Přidat hru
                </button>
            </div>
        </form>

    </div>

    <!-- PRAVE MENU -->

    <div id="pravaCastPridatHru">
        <div class="praveMenu">
            <div class="nadpisMenu">nápověda</div>
        </div>
        <div class="sekce">
            <ul>
                <li>Je záměrem, aby každý larp měl více štítků</li>
                <li>Každý larp musí mít alespoň jeden štítek ze sekce povinné</li>
                <li>Pokud larp nespadá jednoznačně pod jeden štítků, zaškrtněte jich více a upřesněte rozdíl v popisu
                </li>
                <li>Pokud larp označíte štítkem bitva, musíte navíc vybrat jeden ze štítku dřevárna nebo střelné
                    zbraně
                </li>
                <li>Každý nový štítek musí být schválen moderátorem</li>
            </ul>
        </div>
    </div>

    <div style="clear: both"></div>


<%@include file="layout/footer.jsp" %>