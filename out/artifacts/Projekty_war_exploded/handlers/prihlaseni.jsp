<%@ page import="org.pilirion.utils.Pwd" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%
    String mail = request.getParameter("e-mail");
    String pwd = request.getParameter("heslo");

    pwd = Pwd.getMD5(pwd);

    Users users = new Users(conn);
    if (mail != null && pwd != null) {
        User user = users.authenticate(mail, pwd);
        if (user != null) {
            session.setAttribute("csld_user", user);
            out.println("{\"status\": \"ok\"}");
            return;
        }
    }
    if(users.isNameFree(mail)){
        out.println("{\"status\": \"err\", \"message\": \"Uživatel se zadaným jménem neexistuje.\"}");
    } else {
        out.println("{\"status\": \"err\", \"message\": \"Chybně zadané heslo.\"}");
    }
%>