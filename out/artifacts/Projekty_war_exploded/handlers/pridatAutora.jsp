<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.models.user.Group" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="org.pilirion.models.user.Persons" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String authorName = Strings.stringToHTMLString(request.getParameter("jmeno_autora"));
    String authorLastName = Strings.stringToHTMLString(request.getParameter("prijmeni_autora"));
    String authorNickName = Strings.stringToHTMLString(request.getParameter("prezdivka_autora"));
    String authorGroup = Strings.stringToHTMLString(request.getParameter("skupina_autora"));
    int groupId = -1;

    if(!authorName.equals("") && !authorLastName.equals("")){

        if(!authorGroup.equals("")){
            Groups groups = new Groups(conn);
            Group exists = groups.getGroupByName(authorGroup);
            if(exists == null){
                Group group = new Group(-1, authorGroup, "", null);
                groupId = groups.insert(group);
            } else {
                groupId = exists.getId();
            }
        }
        Persons persons = new Persons(conn);
        Users users = new Users(conn);
        List<Person> personsList = persons.getPersonByName(authorName, authorLastName);
        Person toUse;
        Authors authors = new Authors(conn);
        if(personsList.size() > 0){
            toUse = personsList.get(0);
            if(authors.getByPersonId(toUse.getId()) == null){
                User user = users.getById(toUse.getId());

                Author author = new Author(-1, toUse);
                int authorId = authors.insertAuthor(author);
                if(authorId == -1){
                    response.sendRedirect("/pridatHru.jsp?error= " + URLEncoder.encode("Autora se nepovedlo přidat. ", "UTF-8"));
                    return;
                }
                if(groupId != -1){
                    authors.insertAuthorToGroup(authorId, groupId);
                }
                if(user != null) {
                    response.sendRedirect("/pridatHru.jsp?message= " + URLEncoder.encode("Byl přidán nový autor. Jde o uživatele <a href=\"/uzivatel.jsp?id="+user.getId()+"\">"+user.getPerson().getFullName()+"</a>", "UTF-8"));
                } else {
                    response.sendRedirect("/pridatHru.jsp?message= " + URLEncoder.encode("Byl přidán nový autor.", "UTF-8"));
                }
                return;
            }
            response.sendRedirect("/pridatHru.jsp?error= " + URLEncoder.encode("Autor s tímto jménem a příjmením již existuje..", "UTF-8"));
            return;
        } else {
            toUse = new Person(-1, authorName, authorLastName, null, authorNickName, "", "", "", "");
            Author author = new Author(-1, toUse);
            int authorId = authors.insertAuthor(author);
            if(groupId != -1){
                authors.insertAuthorToGroup(authorId, groupId);
            }
            response.sendRedirect("/pridatHru.jsp?message= " + URLEncoder.encode("Byl přidán nový autor.", "UTF-8"));
            return;
        }
    }

    response.sendRedirect("/pridatHru.jsp?error= " + URLEncoder.encode("Autora se nepovedlo přidat. Nebyly zadané všechny potřebné údaje.", "UTF-8"));
%>