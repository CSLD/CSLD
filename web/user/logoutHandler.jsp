<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    session.setAttribute("csld_user", null);

    RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
    request_Dispatcher.forward(request, response);
    return;
%>