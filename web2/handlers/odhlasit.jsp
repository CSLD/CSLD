<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.setAttribute("csld_user", null);
    response.sendRedirect("/index.jsp");
%>