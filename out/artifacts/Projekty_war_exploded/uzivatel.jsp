<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

        <%
        int userId = Integer.parseInt(request.getParameter("id"));
        Users users = new Users(conn);
        User user = users.getById(userId);
        Person person = user.getPerson();
        int personId = user.getPerson().getId();
        Authors authors = new Authors(conn);
        Author author = authors.getByPersonId(personId);
        Games games = new Games(conn);
        List<Game> attended = games.getAttendedGames(user.getId());

    %>
    <!-- GAME -->
    <!-- LEVA CAST -->

    <div class="levaCast" id="uzivatel">
        <table>
            <tr>
                <td>
                    <img src="<%=person.getImage()%>" class="obrazekUzivatel">
                </td>
                <td class="bunkaUzivatel">
                    <div class="jmenoUzivatel"><b><%=person.getNickName()%>
                    </b> <%=person.getFullName()%>
                    </div>
                    <div class="popisek"><%=person.getAge()%> let</div>
                    <div class="popisek"><%=person.getAddress()%>
                    </div>
                    <div class="popisek">Hráč <%=attended.size()%> larpů
                        <%
                            List<Game> authorGames = null;
                            if (author != null) {
                                authorGames = games.getGamesOfAuthor(author.getId());
                                out.println(", tvůrce " + authorGames.size() + " larpů");
                            }
                        %>
                    </div>
                </td>
            </tr>
        </table>
        <div class="obsah">
            <%=person.getDescription()%>
        </div>

        <div class="menuUzivatel">
            <div class="vybranaPolozka">komentáře</div>
        </div>

        <div class="komentare">
            <%
                Comments comments = new Comments(conn);
                List<Comment> userComments = comments.getCommentsOfUser(user.getId());
                for (Comment comment : userComments) {
                    Game commented = games.getById(comment.getGameId());
            %>
            <div class="komentar">
                <table>
                    <tr>
                        <td class="bunkaDatum">
                            <div class="datum"><%=comment.getDate()%>
                            </div>
                        </td>
                        <td class="bunkaKomentar">
                            <span class="nick"><%=user.getPerson().getNickName()%> </span>
                            <span class="jmeno"><%=user.getPerson().getFullName()%></span>
                            o hře <img src="img/ctverecek.png" class="ctverecek"
                                       style="background-color: <%=commented.getRatingColor()%>"><a href="/hra.jsp?id=<%=commented.getId()%>"><%=commented.getName()%></a>
                            <div class="obsahKomentar">
                                <%=comment.getText()%>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <%
                }
            %>
        </div>

    </div>

    <!-- PRAVE MENU -->

    <div class="pravaCast" id="uzivatel">
        <div class="praveMenu">
            <div class="nadpisMenu">uživatelův panel</div>
        </div>

        <div class="sekce">
            <div class="nadpisSekce">Moje larpotéka</div>
            <div class="obsah">
                <%
                    for (Game game : attended) {
                %>
                <div class="obsahPolozka"><img src="img/ctverecek.png" class="ctverecek"
                                               style="background-color: <%=game.getRatingColor()%>"><a
                        href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%> (<%=game.getYear()%>)</a></div>
                <%
                    }
                %>
            </div>
        </div>

        <%
            if (author != null) {
        %>
        <div class="sekce">
            <div class="nadpisSekce">Moje larpy</div>
            <div class="obsah">
                <%
                    for (Game game : authorGames) {
                %>
                <div class="obsahPolozka"><img src="img/ctverecek.png" class="ctverecek"
                                               style="background-color: <%=game.getRatingColor()%>"><a
                        href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%> (<%=game.getYear()%>)</a></div>
                <%
                    }
                %>
            </div>
        </div>
        <%
            }
        %>

    </div>

    <div style="clear: both"></div>


<%@include file="layout/footer.jsp" %>