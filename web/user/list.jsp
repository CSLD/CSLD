<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">

    <%
        String sActualPage = request.getParameter("page");
        int actualPage = (sActualPage != null) ? Integer.valueOf(sActualPage) : 1 ;
        int usersPerPage = 10;
        Users users = new Users(conn);
        int amountOfAllUsers = users.getAllUsers().size();
        List<User> allUsers = users.getUsersOrderedByRating(actualPage, usersPerPage);
    %>
    <jsp:include page="/layout/game/menu.jsp" flush="true" >
        <jsp:param name="amountOfAllGames"
                   value="<%= amountOfAllUsers %>"/>
        <jsp:param name="actualPage"
                   value="<%= actualPage %>" />
        <jsp:param name="gamesPerPage"
                   value="<%= usersPerPage %>" />
        <jsp:param name="baseUrl"
                   value="<%= baseUrl %>" />
    </jsp:include>

    <div id="users">
        <%
            Ratings ratings = new Ratings(conn);
            for(User user: allUsers){
                Rating rating = ratings.getRatingForUser(user.getId());
        %>
        <div name="user" class="user">
            <span name="first_name" class="first_name"><%=user.getPerson().getFirstName()%></span>
            <span name="last_name" class="last_name"><%=user.getPerson().getLastName()%></span> -
            <span name="rating" class="rating"><%=rating.getRating()%></span>
        </div>
        <%
            }
        %>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>