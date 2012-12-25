<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    Authors authors = new Authors(conn);
    int authorId = Integer.parseInt(request.getParameter("id"));
    Author author = authors.getById(authorId);
    if(author == null){
        RequestDispatcher request_Dispatcher=request.getRequestDispatcher(resource.getDefault());
        request_Dispatcher.forward(request, response);
        return;
    }
%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <div id="author">
        <div id="firstName" class="firstName"><%=author.getPerson().getFirstName()%></div>
        <div id="lastName" class="lastName"><%=author.getPerson().getLastName()%></div>
        <div id="nickName" class="nickName"><%=author.getPerson().getNickName()%></div>
        <div id="email" class="email"><%=author.getPerson().getEmail()%></div>
        <div id="games" class="games">
            <%
                Games games = new Games(conn);
                List<Game> allGames = games.getGamesOfAuthor(authorId);
                for(Game game: allGames){
            %>
                <div id="game" class="game"><a href="/game/detail/<%=game.getId()%>"><%=game.getName()%></a></div>
            <%
                }
            %>
        </div>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>