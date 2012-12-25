<%@ page import="org.pilirion.models.game.Label" %>
<%@ page import="org.pilirion.models.game.Labels" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String labelName = Strings.stringToHTMLString(request.getParameter("nazev_stitku"));
    String labelDescription = Strings.stringToHTMLString(request.getParameter("popis_stitku"));

    Labels labels = new Labels(conn);
    if(labelName != null && labelDescription != null && !labelName.equals("") && !labelDescription.equals("")){
        if(labels.getLabelByName(labelName) == null){
            Label label = new Label(-1, labelName, labelDescription, false, false, loggedUser.getId());
            labels.insertLabel(label);
            response.sendRedirect("/pridatHru.jsp?message=" + URLEncoder.encode("Štítek byl úspěšně přidán. Po schválení moderátory ho budou moci používat všichni uživatelé.", "UTF-8"));
            return;
        } else {
            response.sendRedirect("/pridatHru.jsp?error= " + URLEncoder.encode("Štítek se nepovedlo přidat. Štítek se zadaným jménem již existuje.", "UTF-8"));
            return;
        }
    }

    response.sendRedirect("/pridatHru.jsp?error= " + URLEncoder.encode("Štítek se nepovedlo přidat. Nebyly zadané veškeré potřebné údaje.", "UTF-8"));
%>