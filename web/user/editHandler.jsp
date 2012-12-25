<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.utils.DateCsld" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.pilirion.error.Errors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    if(loggedUser == null){
        RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/game/list.jsp");
        request_Dispatcher.forward(request, response);
        return;
    }
    User user = loggedUser;
    Person person = loggedUser.getPerson();
    String name = request.getParameter("userName_name");
    if(name != null && !name.equals("")){
        user.setUserName(name);
    }
    String password = request.getParameter("password_pwd");
    if(password != null && !password.equals("")){
        user.setPassword(password);
    }
    String firstName = request.getParameter("firstName_name");
    if(firstName != null && !firstName.equals("")){
        person.setFirstName(firstName);
    }
    String lastName = request.getParameter("lastName_name");
    if(lastName != null && !lastName.equals("")){
        person.setLastName(lastName);
    }
    String nickName = request.getParameter("nickName_name");
    if(nickName != null && !nickName.equals("")){
        person.setNickName(nickName);
    }
    String mail = request.getParameter("email_mail");
    if(mail != null && !mail.equals("")){
        person.setEmail(mail);
    }
    Date birthDate = DateCsld.parse(request.getParameter("birthDate_date"));
    if(birthDate != null && !birthDate.equals("")){
        person.setBirthDate(birthDate);
    }
    String redirectURL;
    Users users = new Users(conn);
    if(users.editUser(user)){
        session.setAttribute("csld_user", user);
        redirectURL = "/user/detail";
    } else {
        redirectURL = "/user/edit/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
    }
    response.sendRedirect(redirectURL);
%>