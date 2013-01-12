<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@include file="/templates/header.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String name = request.getParameter("nazevHry");
    String labels = request.getParameter("labels").trim();
    String menRole = request.getParameter("menRole");
    String womenRole = request.getParameter("womenRole");
    String bothRole = request.getParameter("bothRole");
    String playersAmount = request.getParameter("playersAmount");
    String hours = request.getParameter("hours");
    String days = request.getParameter("days");
    String year = request.getParameter("year");
    String authors = request.getParameter("authors").trim();
    String description = request.getParameter("description");
    String gameId = request.getParameter("gameId");
    String imageSrc = request.getParameter("imageSrc");
    String gameWeb = request.getParameter("gameWeb");

    int menRoleInt = 0;
    try{
        menRoleInt = Integer.parseInt(menRole);
    } catch (NumberFormatException ex){}

    int womenRoleInt = 0;
    try{
        womenRoleInt = Integer.parseInt(womenRole);
    } catch (NumberFormatException ex){}

    int bothRoleInt = 0;
    try{
        bothRoleInt = Integer.parseInt(bothRole);
    } catch (NumberFormatException ex){}

    int playersAmountInt = 0;
    try{
        playersAmountInt = Integer.parseInt(playersAmount);
    } catch (NumberFormatException ex){}

    int hoursInt = 0;
    try{
        hoursInt = Integer.parseInt(hours);
    } catch (NumberFormatException ex){}

    int daysInt = 0;
    try{
        daysInt = Integer.parseInt(days);
    } catch (NumberFormatException ex){}

    int yearInt = 0;
    try{
        yearInt = Integer.parseInt(year);
    } catch (NumberFormatException ex){}

    int id = -1;
    try{
        id = Integer.parseInt(gameId);
    } catch (NumberFormatException ex){}

    List<String> labelsList = new ArrayList<String>();
    List<String> authorList = new ArrayList<String>();
    String[] labelsNames = labels.split(" / ");
    for(String label: labelsNames){
        if(label.trim().length() > 0){
            labelsList.add(label.trim());
        }
    }
    String[] authorsNames = authors.split(",");
    for(String author: authorsNames){
        if(author.trim().length() > 0){
            authorList.add(author.trim());
        }
    }

    Labels labelsObj = new Labels(conn);
    Authors authorsObj = new Authors(conn);

    List<Label> allLabels = labelsObj.getLabelsByNames(labelsList);
    List<Author> allAuthors = authorsObj.getAuthorsByNames(authorList);

    boolean isRequired = false;
    for(Label label: allLabels){
        if(label.isRequires()){
            isRequired = true;
        }
    }
    if(!isRequired){
        out.println("{\"status\":\"err\", \"message\":\"Musíte zadat alespoň jeden povinný štítek.\"}");
        return;
    }

    Game game = new Game(id, name, imageSrc, menRoleInt, womenRoleInt, bothRoleInt, hoursInt, daysInt, yearInt,
            description, null, playersAmountInt, gameWeb, allAuthors, null, null, allLabels);
    Games games = new Games(conn);
    if(games.editGame(game)){
        out.println("{\"status\": \"ok\"}");
    } else {
        out.println("{\"status\":\"err\", \"message\":\"Nebylo možné hru upravit.\"}");
    }
%>