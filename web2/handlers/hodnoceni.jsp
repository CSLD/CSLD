<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    String rating = request.getParameter("rating");
    String sGameId = request.getParameter("gameId");
    String redirectURL = "/index.jsp";
    if(rating != null && sGameId != null){

        Ratings ratings = new Ratings(conn);
        Rating rRating = new Rating(-1, Integer.parseInt(rating), loggedUser.getId(), Integer.parseInt(sGameId));
        Rating existingRating = ratings.getRatingGameUser(Integer.parseInt(sGameId), loggedUser.getId());
        if(existingRating != null){
            ratings.removeRating(existingRating.getId());
        }
        if(ratings.insertRating(rRating)){
            org.pilirion.models.user.Ratings userRatings = new org.pilirion.models.user.Ratings(conn);
            org.pilirion.models.user.Rating userRating = userRatings.getByType("Rating", loggedUser.getId());
            userRatings.insertRating(userRating);

            redirectURL = "/hra.jsp?id=" + sGameId;
        } else {
            redirectURL = "/hra.jsp?id=" + sGameId;
        }
    }
    response.sendRedirect(redirectURL);
%>