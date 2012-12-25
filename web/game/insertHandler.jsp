<%@ page import="org.pilirion.error.Errors" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.user.Rating" %>
<%@ page import="org.pilirion.models.user.Ratings" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String name = request.getParameter("name_name");
    String web = request.getParameter("web_web");
    String year = request.getParameter("year_year");
    String description = request.getParameter("description_text");
    String iType = request.getParameter("type");
    String[] authors = request.getParameterValues("authors");
    String redirectURL = "/game/insert/" + Errors.INCORRECT_GAME_INFO.getCode() + "/";
    if(name != null && year != null && iType != null){
        Types types = new Types(conn);
        Type type = types.getById(Integer.parseInt(iType));

        List<Author> authorsList = new ArrayList<Author>();
        if(authors != null){
            Authors aAuthors = new Authors(conn);
            Author author;
            for(String sAuthor: authors){
                author = aAuthors.getById(Integer.parseInt(sAuthor));
                authorsList.add(author);
            }
        }

        Game toInsert = new Game(name, -1, Integer.parseInt(year), web, description, type, authorsList, null, null, null);
        Games games = new Games(conn);
        if(games.insertGame(toInsert)){
            Ratings ratings = new Ratings(conn);
            Rating rating = ratings.getByType("Insert", loggedUser.getId());
            ratings.insertRating(rating);
            redirectURL = "/game/list";
        } else {
            redirectURL = "/game/insert/" + Errors.INCORRECT_GAME_INFO.getCode() + "/";
        }
    }
    response.sendRedirect(redirectURL);
%>