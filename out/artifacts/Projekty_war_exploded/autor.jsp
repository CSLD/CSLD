<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">
    <script type="text/javascript">
        var loggedUser = <% if(loggedUser == null) {out.print("false");} else {out.print("true");}%>;
        var role = <% if(loggedUser == null) {out.print("1");} else {out.print(loggedUser.getRole().getId());}%>;
    </script>
    <script type="text/javascript" src="/templates/js/content/autor.js"></script>

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

        <%
        int authorId = Integer.parseInt(request.getParameter("id"));
        Authors authors = new Authors(conn);
        Author author = authors.getById(authorId);
        Person person = author.getPerson();
        Games games = new Games(conn);

    %>
    <!-- GAME -->
    <!-- LEVA CAST -->

    <div class="levaCast" id="uzivatel">
        <%
            if (loggedUser != null && loggedUser.getRole().getId() > 2) {
                Users users = new Users(conn);
                List<User> allUsers = users.getAllUsers();
        %>
            <div class="button" id="editovatAutora" style="cursor: pointer;"><b>Editovat</b></div>
            <div style="display: none;" id="possibleUsers">
                <%
                    for(User user: allUsers){
                %>
                <div class="user"><span class="hidden"><%=user.getId()%></span><%=user.getUserName()%></div>
                <%
                    }
                %>
            </div>
        <%
            }
        %>
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
                    <div class="popisek">
                        <%
                            List<Game> authorGames = null;
                            if (author != null) {
                                authorGames = games.getGamesOfAuthor(author.getId());
                                out.println("tvůrce " + authorGames.size() + " larpů");
                            }
                        %>
                    </div>
                </td>
            </tr>
        </table>
        <div class="obsah">
            <%=person.getDescription()%>
        </div>
    </div>

    <!-- PRAVE MENU -->

    <div class="pravaCast" id="uzivatel">
        <div class="praveMenu">
            <div class="nadpisMenu">Autorův panel</div>
        </div>

        <%
            if (author != null) {
        %>
        <div class="sekce">
            <div class="nadpisSekce">Larpy autora</div>
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