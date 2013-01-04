<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    Authors authors = new Authors(conn);
    List<Author> allAuthors = authors.getAllAuthors();
    String result = "{\"authors\": [";
    for(Author author: allAuthors){
        result += "\"" + author.getPerson().getName().replaceAll("\"","\\\\\"") + "\",";
    }
    if(result != null && !result.equals("")){
        result = Strings.removeLast(result);
    }
    result += "]}";
    out.print(result);
%>