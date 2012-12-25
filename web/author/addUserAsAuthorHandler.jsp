<%@ page import="org.pilirion.error.Errors" %>
<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    String personId = request.getParameter("userId");
    if(personId == null){
        response.sendRedirect("/author/insert/" + Errors.INCORRECT_USER_INFO.getCode() + "/");
    }

    Persons persons = new Persons(conn);
    Person person = persons.getPersonById(Integer.parseInt(personId));
    Author toAdd = new Author(-1, person);
    Authors authors = new Authors(conn);
    String redirectURL;
    if(authors.insertAuthor(toAdd)){
        redirectURL = "/author/insert";
    } else {
        redirectURL = "/author/insert/" + Errors.INCORRECT_USER_INFO.getCode() + "/";
    }
    response.sendRedirect(redirectURL);
%>