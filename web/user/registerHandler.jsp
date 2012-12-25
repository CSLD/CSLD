<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.utils.DateCsld" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.pilirion.error.Errors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String name = request.getParameter("regName_text");
    String password = request.getParameter("regPassword_pwd");
    String firstName = request.getParameter("regFirstName_name");
    String lastName = request.getParameter("regLastName_name");
    String nickName = request.getParameter("regNickName_name");
    String mail = request.getParameter("regMail_mail");
    Date birthDate = DateCsld.parse(request.getParameter("regBirthDate_date"));
    String redirectURL = "/user/register/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
    int USER_ROLE = 2;
    if(name != null && birthDate != null){
        Person person = new Person(-1, firstName, lastName, birthDate, nickName, mail);
        User toRegister = new User(-1, person, name, password, roles.getById(USER_ROLE));
        Users users = new Users(conn);
        User user;
        if(users.insertUser(toRegister)){
            user = users.authenticate(name, password);
            session.setAttribute("csld_user", user);
            redirectURL = "/game/list";
        } else {
            redirectURL = "/user/register/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
        }
    }
    response.sendRedirect(redirectURL);
%>