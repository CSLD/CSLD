<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Search" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@include file="/templates/header.jsp" %>
<%
    String searchQuery = request.getParameter("searchQuery");
    String queryTarget = request.getParameter("queryTarget");
    System.out.println(searchQuery);
    System.out.println(queryTarget);
    // Get all games
    Search searchEngine = new Search(conn);
    List<Game> result;
    if (queryTarget.equals("author")) {
        result = searchEngine.getByAuthor(searchQuery);
    } else if (queryTarget.equals("game")) {
        result = searchEngine.getByGameName(searchQuery);
    } else if (queryTarget.equals("description")) {
        result = searchEngine.getByDescription(searchQuery);
    } else {
        result = searchEngine.getByAll(searchQuery);
    }
    System.out.println(result);
%>
<div id="games">
    <%
        for (Game game : result) {
    %>
    <div name="game" class="game">
        <div name="name" class="name"><a href="/game/detail/<%=String.valueOf(game.getId())%>"><%=game.getName()%>
        </a></div>
        <div name="year" class="year"><%=game.getYear()%>
        </div>
        <div name="description" class="description"><%=game.getDescription()%>
        </div>
        <div name="web" class="web"><%=game.getWeb()%>
        </div>
        <div name="authors" class="authors">
            <% List<Author> authors = game.getAuthors();
                Person person;
                for (Author author : authors) {
                    person = author.getPerson();
            %>
            <div name="author" class="author"><%=person.getFirstName() + " \"" + person.getNickName() + "\" " +
                    person.getLastName()%>
            </div>
            <%
                }
            %>
        </div>
    </div>
    <%
        }
    %>
</div>