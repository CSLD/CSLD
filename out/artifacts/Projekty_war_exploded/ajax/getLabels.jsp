<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Label" %>
<%@ page import="org.pilirion.models.game.Labels" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    Labels labels = new Labels(conn);
    List<Label> allLabels = labels.getLabels();
    String result = "{\"labels\": [";
    boolean optional = false;
    for(Label label: allLabels){
        if(!label.isRequires()){
            optional = true;
            result += "\"" + label.getName() + "\",";
        }
    }
    if(optional){
        result = Strings.removeLast(result);
    }
    result += "], \"required\": [";
    boolean required = false;
    for(Label label: allLabels){
        if(label.isRequires()){
            required = true;
            result += "\"" + label.getName() + "\",";
        }
    }
    if(required){
        result = Strings.removeLast(result);
    }
    result += "]}";
    out.println(result);
%>