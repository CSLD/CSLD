<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%
    String password = request.getParameter("regPassword_pwd");
    String userId = request.getParameter("userId");
    String key = request.getParameter("key");
    String sql = "select * from email_authentication where user_id = " + userId + " and mail_key = " + key;
    Statement stmt = conn.createStatement();
    ResultSet rs;
    rs = stmt.executeQuery(sql);
    boolean correct = false;
    while(rs.next()){
        correct = true;
    }
    if(!correct){
        response.sendRedirect("/game/list");
    }

    String changePwdSql = "update csld_user set password = " + password;
    stmt.execute(changePwdSql);
    response.sendRedirect("/game/list");
%>