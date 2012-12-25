<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- GAME -->
    <!-- LEVA CAST -->

    <script type="text/javascript">
        $(document).ready(function(){
            $("#PridatHru").validate({
                messages: {
                    heslo: "Pole je povinné",
                    heslo_znovu: "Pole je povinné",
                    jmeno: "Pole je povinné",
                    prijmeni: "Pole je povinné",
                    mail: {
                        required: "Pole je povinné",
                        email: "Chybný formát"
                    }
                }
            });

            $("#popis").bind('click', function(){
                if($(this).html() == "Napište něco o sobě"){
                    $(this).html("");
                }
            });
        });
    </script>
    <div class="levaCast">
        <form class="cmxform" id="PridatHru" method="post" enctype="multipart/form-data" action="handlers/registrace.jsp">
            <div class="sekce" style="margin-top: 0;">
                <div class="nadpisStranky"><img src="img/icon/reg_icon.png" class="ikonaStranky" alt="">Registrace</div>
                <table>
                    <%
                        String message = request.getParameter("message");
                        if(message != null){
                    %>
                    <tr>
                        <td style="color: red;" colspan="2" class="popis"><%=message%></td>
                    </tr>
                    <%
                        }
                    %>
                    <tr>
                        <td><input class="textbox required email" type="text" name="mail" id="mail" title="E-mail" value="" ></td>
                        <td class="popis">E-mail slouží zároveň jako login</td>
                    </tr>
                    <tr>
                        <td><input class="textbox required" type="password" name="heslo" id="heslo" title="Heslo" value="" ></td>
                        <td class="popis">Zadejte vaše heslo</td>
                    </tr>
                    <tr>
                        <td><input class="textbox required" type="password" name="heslo_znovu" id="heslo_znovu" title="Heslo znovu" value="" ></td>
                        <td class="popis">Zadejte vaše heslo znovu pro kontrolu</td>
                    </tr>
                    <tr><td><div style="height: 10px;"></div></td></tr>
                    <tr>
                        <td><input type="file" name="logohry" id="uploadfile" style="margin: 0px;" /></td>
                        <td class="popis">Vyberte svoji profilovou fotografii. Maximální velikost fotografie jsou 2MB.</td>
                    </tr>
                    <tr>
                        <td><input class="textbox required" type="text" name="jmeno" id="jmeno" title="Jméno" value="" ></td>
                        <td class="popis">Zadejte své jméno</td>
                    </tr>
                    <tr>
                        <td><input class="textbox required" type="text" name="prijmeni" id="prijmeni" title="Příjmení" value="" ></td>
                        <td class="popis">Zadejte své příjmení</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="nick" id="nick" title="Přezdívka" value="" ></td>
                        <td class="popis">Chcete-li, zadejte svoji přezdívku</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="mesto" id="mesto" title="Město" value="" ></td>  <!-- pridat naseptavac a automatické parovaná s krajem, dostupné pod adresou http://aplikace.mvcr.cz/adresy/ -->
                        <td class="popis">Chcete-li, zadejte město, ve kterém bydlíte (studujete, pracujete, ...)</td>
                    </tr>
                    <tr>
                        <td><input class="textbox" type="text" name="narozeni" id="narozeni" title="dd.mm.yyyy" value="" ></td> <!-- pridat js kalendar, jquery na to nemá rozumné ui -->
                        <td class="popis">Chcete-li, zadejte své datum narození ve formátu DD.MM.YYYY</td>
                    </tr>
                </table>

                <textarea class="textarea" name="popis" id="popis" title="popis_uzivatele" value="" style="width: 90%; height: 100%;" rows="5">Napište něco o sobě</textarea>
                <button class="pridatUzivateleButton" type="submit" name="pridat_uzivatele" title="Registrovat">Registrovat</button>
            </div>
        </form>

    </div>

    <!-- PRAVE MENU -->

    <div class="pravaCast">
        <div class="praveMenu">
            <div class="nadpisMenu">nápověda</div>
        </div>
        <div class="sekce">
            Vítejte na česko-slovenské larpové databázi. Po vyplnění krátkého formuláře vlevo se stanete registrovaným uživatelem a můžete tak využít všech funkcí jako hodnocenía a komentování jednotlivých larpů, stejně jako jejich zařazení do vaší larpotéky.
        </div>
    </div>

    <div style="clear: both"></div>


<%@include file="layout/footer.jsp" %>