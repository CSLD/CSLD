<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">

    <%
        String sActualPage = request.getParameter("page");
        int actualPage = (sActualPage != null) ? Integer.valueOf(sActualPage) : 1 ;
        int authorsPerPage = settingsCsld.getGamesPerPage();
        Authors authors = new Authors(conn);
        int amountOfAllGames = authors.getAllAuthors().size();
        List<Author> allAuthors = authors.getAllAuthors(actualPage, authorsPerPage);
    %>
    <jsp:include page="/layout/game/menu.jsp" flush="true" >
        <jsp:param name="amountOfAllGames"
                   value="<%= amountOfAllGames %>"/>
        <jsp:param name="actualPage"
                   value="<%= actualPage %>" />
        <jsp:param name="gamesPerPage"
                   value="<%= authorsPerPage %>" />
        <jsp:param name="baseUrl"
                   value="<%= baseUrl %>" />
    </jsp:include>

    <div id="authors">
        <%
            Person person;
            for(Author author: allAuthors){
                person = author.getPerson();
        %>
        <div name="author" class="Author">
            <div name="name" class="name"><a href="/author/detail/<%=String.valueOf(author.getId())%>"><%=person.getName()%></a></div>
        </div>
        <%
            }
        %>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>