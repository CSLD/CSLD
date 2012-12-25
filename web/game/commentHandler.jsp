<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="java.sql.Date" %>
<%@ page import="org.pilirion.models.user.Ratings" %>
<%@ page import="org.pilirion.models.user.Rating" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String commentText = request.getParameter("comment_text");
    String sGameId = request.getParameter("gameId");
    String redirectURL = "/game/list/";
    if(commentText != null && sGameId != null){
        Comments comments = new Comments(conn);
        Comment comment = new Comment(-1, new Date(new java.util.Date().getTime()), commentText,
                loggedUser.getId(), Integer.parseInt(sGameId));
        if(comments.insertComment(comment)){
            Ratings ratings = new Ratings(conn);
            Rating rating = ratings.getByType("Comment", loggedUser.getId());
            ratings.insertRating(rating);
            redirectURL = "/game/detail/" + sGameId;
        } else {
            redirectURL = "/game/detail/" + sGameId;
        }
    }
    response.sendRedirect(redirectURL);
%>