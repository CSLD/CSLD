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
    <link rel="stylesheet" type="text/css" href="/templates/css/js.css" />

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
        <div id="autorId" class="hidden"><%=authorId%></div>
        <%
            if (loggedUser != null && loggedUser.getRole().getId() > 2) {
                Persons persons = new Persons(conn);
                List<Person> allPersons = persons.getPersonsFromDb("select * from person");
        %>
            <div class="button" id="editovatAutora" style="cursor: pointer;"><b>Editovat</b></div>
            <div style="display: none;" id="possibleUsers">
                <table>
                <%
                    for(Person person1: allPersons){
                %>
                <tr class="user"><td><span class="hidden"><%=person1.getId()%></span></td><td><%=person1.getName()%></td>
                    <td><%=person1.getAddress()%></td><td><%=person1.getEmail()%></td></tr>
                <%
                    }
                %>
                </table>
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
                    <div class="jmenoUzivatel"><b><span id="nickName"><%=person.getNickName()%></span>
                    </b> <span id="fullName"><%=person.getFullName()%></span>
                    </div>
                    <div class="popisek"><span id="age"><%=person.getAge()%></span> let</div>
                    <div class="popisek"><span id="address"><%=person.getAddress()%></span>
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
            <span id="description"><%=person.getDescription()%></span>
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