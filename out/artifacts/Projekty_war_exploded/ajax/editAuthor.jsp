<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@include file="/templates/header.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String authorId = request.getParameter("authorId");
    String userId = request.getParameter("userId");
%>