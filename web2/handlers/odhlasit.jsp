<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.setAttribute("csld_user", null);
    Cookie killMyCookie = new Cookie("csldUserLoginCookie", null);
    killMyCookie.setMaxAge(0);
    killMyCookie.setPath("/");
    response.addCookie(killMyCookie);
    response.sendRedirect("/index.jsp");
%>