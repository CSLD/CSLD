<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
    request_Dispatcher.forward(request, response);
    return;
%>