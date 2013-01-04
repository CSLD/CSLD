<%@ page import="org.pilirion.models.game.Comment" %>
<%@ page import="org.pilirion.models.game.Comments" %>
<%@ page import="org.pilirion.models.user.Rating" %>
<%@ page import="org.pilirion.models.user.Ratings" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    String commentText = Strings.stringToHTMLString(request.getParameter("comment_text"));
    String sGameId = request.getParameter("gameId");
    String redirectURL = "/index.jsp";
    if (commentText != null && sGameId != null) {
        Comments comments = new Comments(conn);
        Comment comment = new Comment(-1, new java.sql.Date(new java.util.Date().getTime()), commentText,
                loggedUser.getId(), Integer.parseInt(sGameId));
        if (comments.getCommentGameUser(loggedUser.getId(), Integer.parseInt(sGameId)) == null) {
            if (comments.insertComment(comment)) {
                Ratings ratings = new Ratings(conn);
                Rating rating = ratings.getByType("Comment", loggedUser.getId());
                ratings.insertRating(rating);
                redirectURL = "/hra.jsp?id=" + sGameId;
            } else {
                redirectURL = "/hra.jsp?id=" + sGameId;
            }
        } else {
            redirectURL = "/hra.jsp?id=" + sGameId;
        }
    }
    response.sendRedirect(redirectURL);
%>