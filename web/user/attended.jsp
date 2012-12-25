<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.Author" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">

    <%
        String sActualPage = request.getParameter("page");
        int actualPage = (sActualPage != null) ? Integer.valueOf(sActualPage) : 1 ;
        int gamesPerPage = settingsCsld.getGamesPerPage();
        Games games = new Games(conn);
        int amountOfAllGames = games.getAttendedGames(loggedUser.getId()).size();
        List<Game> allGames = games.getAttendedGames(loggedUser.getId(),actualPage, gamesPerPage);
    %>
    <jsp:include page="/layout/user/menu.jsp" flush="true" >
        <jsp:param name="amountOfAllGames"
                   value="<%= amountOfAllGames %>"/>
        <jsp:param name="actualPage"
                   value="<%= actualPage %>" />
        <jsp:param name="gamesPerPage"
                   value="<%= gamesPerPage %>" />
        <jsp:param name="baseUrl"
                   value="<%= baseUrl %>" />
    </jsp:include>

    <div id="games">
        <%
            for(Game game: allGames){
        %>
        <div name="game" class="game">
            <div name="name" class="name"><a href="/game/detail/<%=String.valueOf(game.getId())%>"><%=game.getName()%></a></div>
            <div name="year" class="year"><%=game.getYear()%></div>
            <div name="description" class="description"><%=game.getDescription()%></div>
            <div name="web" class="web"><%=game.getWeb()%></div>
            <div name="authors" class="authors">
                <% List<Author> authors = game.getAuthors();
                    Person person;
                    for(Author author: authors){
                        person = author.getPerson();
                %>
                <div name="author" class="author"><%=person.getFirstName() + " \"" + person.getNickName() + "\" " +
                        person.getLastName()%></div>
                <%
                    }
                %>
            </div>
        </div>
        <%
            }
        %>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>