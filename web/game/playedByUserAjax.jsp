<%@ page import="org.pilirion.models.game.Games" %>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    String gameIdRequest = request.getParameter("gameId");
    String userIdRequest = request.getParameter("userId");

    if(gameIdRequest == null || userIdRequest == null){
        out.println("{\"status\": \"err\"}");
        return;
    }

    int gameId = Integer.parseInt(gameIdRequest);
    int userId = Integer.parseInt(userIdRequest);

    Games games = new Games(conn);
    if(games.isUserPlayerOfGame(userId,gameId)){
        games.removeUserAsPlayerOfGame(userId,gameId);
    } else {
        games.setUserAsPlayerOfGame(userId,gameId);
    }

    out.println("{\"status\": \"ok\"}");
%>
