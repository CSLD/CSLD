<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

<!-- HEADER -->
<%@include file="layout/head.jsp" %>

<%
    String fullText = request.getParameter("searchGame");
    Labels labels = new Labels(conn);
    List<Label> availableLabels = labels.getLabels();
    List<String> usedLabels = new ArrayList<String>();
    for(Label label: availableLabels){
        if(request.getParameter(label.getName()) != null){
            usedLabels.add(request.getParameter(label.getName()));
        }
    }
    String minPlayers = request.getParameter("min_hracu");
    String maxPlayers = request.getParameter("max_hracu");
    String minHours = request.getParameter("min_hod");
    String maxHours = request.getParameter("max_hod");
    String minDays = request.getParameter("min_dnu");
    String maxDays = request.getParameter("max_dnu");
    Search search = new Search(conn);
    List<Game> foundGames = search.findGamesByParams(fullText, usedLabels, minPlayers, maxPlayers, minHours, maxHours,
        minDays, maxDays);
    List<Game> firstFoundGames;
    List<Game> restGames;
    if(foundGames.size() > 3){
        firstFoundGames = foundGames.subList(0,3);
        restGames = foundGames.subList(3,foundGames.size());
    } else {
        firstFoundGames = foundGames;
        restGames = new ArrayList<Game>();
    }

    List<Author> firstFoundAuthors;
    List<Author> restAuthors;
    if(fullText == null){
        fullText = "";
    }

    if(fullText != null){
        List<Author> foundAuthors = search.getAuthorByText(fullText);
        if(foundAuthors.size() > 3){
            firstFoundAuthors = foundAuthors.subList(0,3);
            restAuthors = foundAuthors.subList(3, foundAuthors.size());
        } else {
            firstFoundAuthors = foundAuthors;
            restAuthors = new ArrayList<Author>();
        }
    } else {
        firstFoundAuthors = new ArrayList<Author>();
        restAuthors = new ArrayList<Author>();
    }

%>
<!-- GAME -->
<!-- LEVA CAST -->

<div class="levaCast" id="vyhledavani">
    <div class="sekce">
        <div class="nadpisStranky"><img src="img/icon/glass_icon.png" class="ikonaStranky">Vyhledávání</div>
        <div class="levaCastVyhledavani">
            <table class="prvniTabulka">
                <%
                    for(Game game: firstFoundGames){
                %>
                <tr>
                    <td>
                        <a href="/hra.jsp?id=<%=game.getId()%>"><img src="<%=game.getImage()%>" class="obrazekVyhledavani"></a><br />
                    </td>
                    <td class="obsah">
                        <img src="img/ctverecek.png" class="ctverecek" style="background-color: <%=game.getRatingColor()%>"></span>
                        <span class="nadpisHra"><a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%></a></span>
                        <div class="popisek">
                        <%
                            String result = game.getLabelsText() + "<br />"  + game.getAuthorsText();
                            if(result.length() > 106){
                                result = result.substring(0, 106) + " ... <a href='hra.jsp?id="+game.getId()+"'>více</a>";
                            }
                            out.println(result);
                        %>
                        </div>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>

            <div class="dalsiVysledky">Další výsledky:</div>

            <table class="druhaTabulka">
                <%
                    for(Game game: restGames){
                %>
                <tr>
                    <td>
                        <a href="/hra.jsp?id=<%=game.getId()%>" class="hraStrucna"><%=game.getName()%></a> (<%=game.getYear()%>)
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </div>

        <div class="pravaCastVyhledavani">
            <table class="prvniTabulka">
                <%
                    for(Author user: firstFoundAuthors){
                        Person person = user.getPerson();
                %>
                <tr>
                    <td>
                        <a href="/autor.jsp?id=<%=user.getId()%>"><img src="<%=person.getImage()%>" class="obrazekVyhledavani"></a><br />
                    </td>
                    <td class="obsah">
                        <a href="/autor.jsp?id=<%=user.getId()%>"><span class="nick"><%=person.getNickName()%></span><span class="jmeno">
                            <%=person.getFirstName() + " " + person.getLastName()%></span></a>
                        <div class="popisek"><%=person.getAge()%> let<br />
                            <%=person.getAddress()%></div>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>

            <div class="dalsiVysledky">Další výsledky:</div>

            <table class="druhaTabulka">
                <%
                    for(Author user: restAuthors){
                        Person person = user.getPerson();
                %>
                <tr>
                    <td>
                        <a href="/autor.jsp?id=<%=user.getId()%>"><span class="nick"><%=person.getNickName()%></span><span class="jmeno">
                            <%=person.getFirstName() + " " + person.getLastName()%></span></a>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </div>

        <div style="clear: both"></div>


    </div>
</div>


<!-- PRAVE MENU -->

<div class="pravaCast" id="Zebricky">
    <form id="zebrickyForm" method="get" action="/hledani.jsp">
        <div class="praveMenu">
        <div class="nadpisMenu">filtry</div>
    </div>

        <%
            if(loggedUser != null){
        %>
        <div class="sekce">
            <table>
                <tr>
                    <td>
                        <a href="/pridatHru.jsp"><img src="/img/icon/plus_icon.png" class="pridatIkona"></a>
                    </td>
                    <td class="pridatPopis">
                        <a href="/pridatHru.jsp">Přidat hru</a>
                    </td>
                </tr>
            </table>
        </div>
        <%
        } else {
        %>
        <div class="sekce">
            Pro přidání hry je potřeba se nejprve zaregistrovat.
        </div>
        <%
            }
        %>
    <div class="sekce">
        <div class="nadpisFiltr">Počty hráčů</div>
        <div class="hraciFiltr">
            <input class="textbox" type="text" name="min_hracu" id="min_hracu" title="min" value="" style="width: 60px;" >
            <input class="textbox" type="text" name="max_hracu" id="max_hracu" title="max" value="" style="width: 60px;"></div>
        <div class="nadpisFiltr">Délka hry</div>
        <div class="delkaFiltr">
            <input class="textbox" type="text" name="min_hod" id="min_hod" title="min hod" value="" style="width: 60px;">
            <input class="textbox" type="text" name="max_hod" id="max_hod" title="max hod" value="" style="width: 60px;">
            <input class="textbox" type="text" name="min_dnu" id="min_dnu" title="min dnů" value="" style="width: 60px;">
            <input class="textbox" type="text" name="max_dnu" id="max_dnu" title="max dnů" value="" style="width: 60px;"></div>
    </div>
    <div class="sekce">
        <div class="nadpisFiltr">Štítky</div>

        <fieldset class="fieldset">
            <ul class="checklist">
                <%
                    List<Label> requiredLabels = labels.getRequiredLabels();
                    List<Label> otherLabels = labels.getOtherLabels();
                    for (Label label : requiredLabels) {
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
                <div style="height:12px; "></div>
                <%
                    for (Label label : otherLabels) {
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
        <button class="filtrovatButton" type="submit" name="filtrovat" title="filtrovat">Filtrovat</button>
    </div>
        </form>
</div>

<div style="clear: both"></div>



<%@include file="layout/footer.jsp" %>