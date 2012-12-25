<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <%
        String sActualPage = request.getParameter("page");
        int actualPage = (sActualPage != null) ? Integer.valueOf(sActualPage) : 1 ;
        int gamesPerPage = settingsCsld.getGamesPerPage();
        Ratings ratings = new Ratings(conn);
        int amountOfAllGames = ratings.getAllRatingsForUser(loggedUser.getId()).size();

        List<Rating> allRatings = ratings.getAllRatingsForUser(loggedUser.getId(), actualPage, gamesPerPage);
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

    <div id="ratings">
        <%
            for(Rating rating: allRatings){
        %>
        <div name="rating" class="ratings">
            <span name="description" class="description"><%=rating.getDescription()%></span> -
            <span name="value" class="value"><%=rating.getRating()%></span>
        </div>
        <%
            }
        %>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>