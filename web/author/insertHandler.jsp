<%@ page import="org.pilirion.error.Errors" %>
<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.utils.DateCsld" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String firstName = request.getParameter("regFirstName_name");
    String lastName = request.getParameter("regLastName_name");
    String nickName = request.getParameter("regNickName_name");
    String mail = request.getParameter("regMail_mail");
    Date birthDate = null;
    if(request.getParameter("regBirthDate_date") != null){
        birthDate = DateCsld.parse(request.getParameter("regBirthDate_date"));
    }

    String redirectURL;
    Person person = new Person(-1, firstName, lastName, birthDate, nickName, mail);
    Author toAdd = new Author(-1, person);
    Authors authors = new Authors(conn);
    if(authors.insertAuthor(toAdd)){
        redirectURL = "/author/insert";
    } else {
        redirectURL = "/author/insert/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
    }
    response.sendRedirect(redirectURL);
%>