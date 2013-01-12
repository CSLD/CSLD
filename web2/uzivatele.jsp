<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- GAME -->
    <!-- LEVA CAST -->

    <div class="levaCast" id="Uzivatele">
        <div class="sekce sekceUzivatele">
            <div class="nadpisStranky"><img src="img/icon/user_icon.png" class="ikonaStranky">Uživatelé</div>
            <table>
                <%
                    Users users = new Users(conn);
                    Games games = new Games(conn);
                    List<User> allUsers = users.getUsersOrderedByComments();
                    for(User user: allUsers){
                        int attendedGames = games.getAttendedGames(user.getId()).size();
                        int commentedGames = games.getCommentedGames(user.getId()).size();
                        Person person = user.getPerson();
                %>
                <tr class="polozkaUzivatel">
                    <td class="larp"><a href="uzivatel.jsp?id=<%=user.getId()%>"><span class="uzivatelPrezdivka"><%=person.getNickName()%></span><span class="uzivatelJmeno"> <%=person.getFullName()%></span></a></td>
                    <td class="pocetherUzivatel komentare"><%=commentedGames%> komentovaných her</td>
                    <td class="pocetherUzivatel"><%=attendedGames%> odehraných her</td>
                </tr>
                <%
                    }
                %>
            </table>

        </div>
    </div>


    <!-- PRAVE MENU -->

    <div class="pravaCast" id="Uzivatele">
        <div class="praveMenu">
            <div class="nadpisMenu">Uživatelé</div>
        </div>
        <div class="sekce">
            <table>
                <tr>
                    <td>
                        <a href="/pridatHru.jsp"><img src="img/icon/plus_icon.png" class="pridatIkona"></a>
                    </td>
                    <td class="pridatPopis">
                        <a href="/pridatHru.jsp">Přidat hru</a>
                    </td>
                </tr>
            </table>
        </div>
        <%
            User larpKing = users.getLarpKing();
            if(larpKing != null){
                Person person = larpKing.getPerson();
                List<Game> kingGames = games.getAttendedGames(larpKing.getId());
        %>
        <div class="sekce">
            <div class="nadpisSekce"><a href="uzivatel.jsp?id=<%=larpKing.getId()%>">Král larpu</a></div>
            <table>
                <tr>
                    <td>
                        <a href="uzivatel.jsp?id=<%=larpKing.getId()%>"><img src="<%=person.getImage()%>" class="obrazekUzivatel"></a>
                    </td>
                    <td class="obsah">
                        <a href="uzivatel.jsp?id=<%=larpKing.getId()%>"><span class="nick"><%=person.getNickName()%></span> <span class="jmeno"><%=person.getFullName()%></span></a>
                        <div class="popisekUzivatel">ocenění za největší počet odehraných larpů</div>
                        <div class="popisekUzivatel"><%=kingGames.size()%> odehraných her</div>
                    </td>
                </tr>
            </table>
        </div>
        <%
            }
        %>
        <%
            User kingCommenter = users.getKingCommenter();
            if(kingCommenter != null){
                Person person = kingCommenter.getPerson();
                int commentedGames = games.getCommentedGames(kingCommenter.getId()).size();
        %>
        <div class="sekce">
            <div class="nadpisSekce"><a href="uzivatel.jsp?id=<%=kingCommenter.getId()%>">Nejaktivnější komentátor</a></div>
            <table>
                <tr>
                    <td>
                        <a href="uzivatel.jsp?id=<%=kingCommenter.getId()%>"><img src="<%=person.getImage()%>" class="obrazekUzivatel"></a>
                    </td>
                    <td class="obsah">
                        <a href="uzivatel.jsp?id=<%=kingCommenter.getId()%>"><span class="nick"><%=person.getNickName()%></span> <span class="jmeno"><%=person.getFullName()%></span></a>
                        <div class="popisekUzivatel">ocenění za největší počet komentářů</div>
                        <div class="popisekUzivatel"><%=commentedGames%> komentovaných her</div>
                    </td>
                </tr>
            </table>
        </div>
        <%
            }
        %>
    </div>

    <div style="clear: both"></div>



<%@include file="layout/footer.jsp" %>