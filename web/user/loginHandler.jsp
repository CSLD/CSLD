<%@ page import="org.pilirion.models.user.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    String name = request.getParameter("loginName_text");
    String password = request.getParameter("loginPassword_pwd");

    Users users = new Users(conn);
    if(name != null && password != null){
        User user = users.authenticate(name, password);
        if(user != null){
            session.setAttribute("csld_user", user);
        }
    }

    RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
    request_Dispatcher.forward(request, response);
    return;
%>