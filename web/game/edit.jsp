<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    if(loggedUser == null){
        RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
        request_Dispatcher.forward(request, response);
        return;
    }
%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<%
    String id = request.getParameter("id");
    if(id != null){
        Games games = new Games(conn);
        Game game = games.getById(Integer.parseInt(id));
        if(game == null){
            RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
            request_Dispatcher.forward(request, response);
            return;
        }
    }
%>
<div id="content">
    <form name="gameForm"></form>
</div>
