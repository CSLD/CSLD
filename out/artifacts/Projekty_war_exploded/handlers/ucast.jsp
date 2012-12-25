<%@ page import="org.pilirion.models.game.Games" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    int userId = loggedUser.getId();
    String gameIdRequest = request.getParameter("gameId");
    if(gameIdRequest == null){
        response.sendRedirect("/index.jsp");
        return;
    }

    int gameId = Integer.parseInt(gameIdRequest);
    Games games = new Games(conn);
    if(games.isUserPlayerOfGame(userId,gameId)){
        games.removeUserAsPlayerOfGame(userId,gameId);
    } else {
        games.setUserAsPlayerOfGame(userId,gameId);
    }

    response.sendRedirect("/hra.jsp?id=" + gameIdRequest);
%>