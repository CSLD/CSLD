<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <%
        Settings settings = new Settings(conn, loggedUser.getId());
    %>
    <%@include file="/layout/user/menuItems.jsp"%>
    <form method="post" action="/user/settingsHandler.jsp">
        <div id="user">
            <div name="formField">
                <span class="label">Počet zobrazených her na stránce: </span>
                <input type="text" id="o_gamesPerPage_number" name="gamesPerPage_number" value="<%=settings.getGamesPerPage()%>" />
                <span id="gamesPerPageError" class="error"></span>
            </div>
            <input type="hidden" name="userId" value="<%=loggedUser.getId()%>"/>
            <input type="submit" value="Edit" />
        </div>
    </form>
</div>
<%@include file="/layout/footer.jsp"%>