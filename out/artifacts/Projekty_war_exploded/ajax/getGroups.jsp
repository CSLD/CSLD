<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    Groups groups = new Groups(conn);
    List<Group> allGroups = groups.getGroups();
    String result = "{\"groups\": [";
    for(Group group: allGroups){
        result += "\"" + group.getName() + "\",";
    }
    if(result != null && !result.equals("")){
        result += Strings.removeLast(result);
    }
    result += "]}";
%>