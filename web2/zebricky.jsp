<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Comment" %>
<%@ page import="org.pilirion.models.game.Comments" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

<!-- HEADER -->
<%@include file="layout/head.jsp" %>

<!-- GAME -->
<!-- LEVA CAST -->
<%
    request.setCharacterEncoding("UTF-8");
    String fullText = request.getParameter("searchGame");
    Labels labels = new Labels(conn);
    List<Label> availableLabels = labels.getLabels();
    List<String> usedLabels = new ArrayList<String>();
    for(Label label: availableLabels){
        if(request.getParameter(label.getName()) != null){
            usedLabels.add(label.getName());
        }
    }
    String minPlayers = request.getParameter("min_hracu");
    String maxPlayers = request.getParameter("max_hracu");
    String minHours = request.getParameter("min_hod");
    String maxHours = request.getParameter("max_hod");
    String minDays = request.getParameter("min_dnu");
    String maxDays = request.getParameter("max_dnu");
    Search search = new Search(conn);
    List<Game> topGames = search.findGamesByParamsOrderByRating(fullText, usedLabels, minPlayers, maxPlayers, minHours, maxHours,
        minDays, maxDays);
%>

<div class="levaCast" id="Zebricky">
    <div class="sekce sekceZebricky">
        <div class="nadpisStranky"><img src="img/icon/charts_icon.png" class="ikonaStranky">Žebříčky</div>
        <table>
            <%
                int place = 1;
                for(Game game: topGames){
                    List<Rating> ratings = game.getRatings();
                    List<Comment> comments = game.getComments();
                    int idGame = game.getId();
                    Ratings ratingsUser = new Ratings(conn);
                    Rating ratingUser = null;
                    Comments commentsUser = new Comments(conn);
                    Comment commentUser = null;
                    if (loggedUser != null) {
                    int idUser = loggedUser.getId();
                    ratingUser = ratingsUser.getRatingGameUser(idGame, loggedUser.getId());
                    commentUser = commentsUser.getCommentGameUser(idUser,idGame);
                    }
            %>
            <tr class="polozkaZebricek">
                <td class="poradi"><%=place%></td>
                <td class="larp"><a href="hra.jsp?id=<%=game.getId()%>" class="larpOdkaz"><%=game.getName()%></a> (<%=game.getYear()%>)</td>
                <td class="hodnoceni"><%if (ratings.size()>3) {
                out.print(game.getRatingPercents() + "&thinsp;%");
                }
                else {
                out.print("?&thinsp;%");
                }
                %></td>
                <td class="pocet_hodnoceni"><%=ratings.size()%>
                 <%
                    // zobrazovani cervenych a modrych ikon u hodnocenych her
                          if (ratingUser != null) {
                          %>
                          <img src="img/icon/star_icon_red.png" style="position:relative; top:2px;">
                          <%
                          }
                          else {
                          %><img src="img/icon/star_icon.png" style="position:relative; top:2px;">
                          <%
                          }
                          %>
                          <%=comments.size()%>
                          <%
                     // zobrazovani cervenych a modrych ikon u komentovanych her
                          if ( commentUser != null ) {
                          %>
                          <img src="img/icon/comment_icon_red.png" style="position:relative; top:3px;">
                          <%
                          }
                          else {
                          %>
                          <img src="img/icon/comment_icon.png" style="position:relative; top:3px;">
                          <%
                          }
                          %>

                </td>
            </tr>
            <%
                    place++;
                }
            %>
        </table>

    </div>
</div>


<!-- PRAVE MENU -->

<div class="pravaCast" id="Zebricky">
    <form id="zebrickyForm" method="get" action="/zebricky.jsp">

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