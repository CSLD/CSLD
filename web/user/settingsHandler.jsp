<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.utils.DateCsld" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.pilirion.error.Errors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String userIdRequest = request.getParameter("userId");
    int userId = Integer.parseInt(userIdRequest);
    Settings settings = new Settings(conn, userId);
    String gamesPerPageRequest = request.getParameter("gamesPerPage_number");
    if(gamesPerPageRequest != null && !gamesPerPageRequest.equals("")){
        int gamesPerPage = Integer.parseInt(gamesPerPageRequest);
        String redirectURL;
        if(settings.setGamesPerPage(gamesPerPage)){
            redirectURL = "/user/settings";
        } else {
            redirectURL = "/user/settings/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
        }
        response.sendRedirect(redirectURL);
    }
%>