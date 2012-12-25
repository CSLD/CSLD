<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    String userName = request.getParameter("userName");
    Users users = new Users(conn);
    if(users.isNameFree(userName)){
        out.println("{\"free\": \"true\"}");
    } else {
        out.println("{\"free\": \"false\"}");
    }
%>