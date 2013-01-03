<%@ page import="org.pilirion.models.game.*" %>
<%@include file="/templates/header.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String authorId = request.getParameter("authorId");
    String userId = request.getParameter("userId");

    if(userId == null || userId.equals("") || authorId == null || authorId.equals("")){
        out.print("{\"status\": \"err\",\"message\":\"Nebyly zadané všechny údaje.\"}");
        return;
    }

    Authors authors = new Authors(conn);
    if(authors.editUser(Integer.parseInt(userId), Integer.parseInt(authorId))){
        out.print("{\"status\": \"ok\"}");
    } else {
        out.print("{\"status\": \"err\",\"message\":\"Chybná editace uživatele.\"}");
    }
%>