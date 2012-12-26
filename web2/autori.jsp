<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.user.Groups" %>
<%@ page import="org.pilirion.models.user.Group" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- GAME -->
    <!-- LEVA CAST -->

    <div class="levaCast" id="Autori">
        <div class="sekce sekceAutori">
            <div class="nadpisStranky"><img src="img/icon/light_icon.png" class="ikonaStranky">Autoři</div>
            <table>
                <%
                    Authors authors = new Authors(conn);
                    Games games = new Games(conn);
                    List<Author> authorsList = authors.getAllAuthors();
                    String nickName, name;
                    Game game;
                    Person person;
                    for(Author author: authorsList) {
                        game = games.getBestGameOfAuthor(author.getId());
                        person = author.getPerson();
                        nickName = (person != null) ? person.getNickName() : "";
                        name = (person != null) ? person.getFirstName() + " " + person.getLastName() : "";
                %>
                <tr class="polozkaAutor">
                    <td class="larp"><a href="/autor.jsp?id=<%=author.getId()%>"><span class="autorPrezdivka"><%=nickName%></span><span class="AutorJmeno"> <%=name%></span></a></td>
                    <td class="nejlepsiHraAutor">
                    <%
                        if(game != null) {
                    %>
                   <a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%> (<%=game.getRatingPercents()%>%)</a>
                    <%
                        }
                    %>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>

        </div>
    </div>


    <!-- PRAVE MENU -->

    <div class="pravaCast" id="Autori">
        <div class="praveMenu">
            <div class="nadpisMenu">Nejaktivnější tvůrci</div>
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

        <%
            Author bestAuthor = authors.getBestAuthor();
            System.out.println(bestAuthor);
            if(bestAuthor != null){
                Game bestGame = games.getBestGameOfAuthor(bestAuthor.getId());
                System.out.println(bestGame);
                if(bestGame != null){
        %>
        <div class="sekce">
            <div class="nadpisSekce">Nejlepší tvůrce</div>
            <table>
                <tr>
                    <td>
                        <a href="/autor.jsp?id=<%=bestAuthor.getId()%>"><img src="<%=bestAuthor.getPerson().getImage()%>" class="obrazekUzivatel"></a>
                    </td>
                    <td class="obsah">
                        <a href="/autor.jsp?id=<%=bestAuthor.getId()%>"><span class="nick"><%=bestAuthor.getPerson().getNickName()%></span>
                            <span class="jmeno"><%=bestAuthor.getPerson().getFirstName() + " " + bestAuthor.getPerson().getLastName()%></span></a>
                        <div class="popisekAutor">nejlepší larp: <%=bestGame.getName()%> (<%=bestGame.getRatingPercents()%>%)</div>
                    </td>
                </tr>
            </table>
        </div>
        <%
                }
            }
            Groups groups = new Groups(conn);
            Group bestGroup = groups.getGroupWithBiggestProduction();
            if(bestGroup != null){
                List<Game> gamesGroup = games.getGamesByGroup(bestGroup.getId());
        %>
        <div class="sekce">
            <div class="nadpisSekce">Největší produkce</div>
            <table>
                <tr>
                    <td>
                        <a href="group.jsp?id=<%=bestGroup.getId()%>"><img src="<%=bestGroup.getImage()%>" class="obrazekUzivatel"></a>
                    </td>
                    <td class="obsah">
                        <a href="group.jsp?id=<%=bestGroup.getId()%>"><span class="nick"><%=bestGroup.getName()%></span> <span class="jmeno"></span></a>
                        <div class="popisekAutor"><%=gamesGroup.size()%> vytvořených larpů</div>
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