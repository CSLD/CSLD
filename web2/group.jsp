<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.user.Group" %>
<%@ page import="org.pilirion.models.user.Groups" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- GAME -->

    <!-- LEVA CAST -->
    <%
        String gameId = request.getParameter("id");
        if(gameId == null){
            response.sendRedirect("/index.jsp");
            return;
        }
        Groups groups = new Groups(conn);
        Group group = groups.getById(Integer.parseInt(gameId));
        List<Author> authors = group.getAuthors();
        int authorsSize = authors.size();
        int leftAuthors = (int) Math.ceil(authorsSize / 2);
        int rightAuthors = (int) Math.floor(authorsSize / 2);

    %>

    <div class="levaCast" id="skupina">
        <div class="sekce">
            <div class="nadpisStranky"><img src="img/icon/group_icon.png" class="ikonaStranky"><%=group.getName()%></div>
            <div class="levaCastSkupina">
                <table class="Tabulka">
                    <%
                        for(int i = 0; i < leftAuthors; i++){
                            Author author = authors.get(i);
                            Person person = author.getPerson();
                    %>

                    <tr>
                        <td>
                            <a href="/autor.jsp?id=<%=author.getId()%>"><img src="<%=person.getImage()%>" class="obrazekUzivatel"></a><br />
                        </td>
                        <td class="obsah">
                            <a href="/autor.jsp?id=<%=author.getId()%>"><span class="nick"><%=person.getNickName()%></span><span class="jmeno"><%=person.getFullName()%></span></a>
                            <div class="popisek"><%=person.getAge()%> let<br />
                                <%=person.getAddress()%></div>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </table>
            </div>

            <div class="pravaCastSkupina">
                <table class="Tabulka">
                    <%
                        for(int i = leftAuthors; i < authorsSize; i++){
                            Author author = authors.get(i);
                            Person person = author.getPerson();
                    %>

                    <tr>
                        <td>
                            <a href="/autor.jsp?id=<%=author.getId()%>"><img src="<%=person.getImage()%>" class="obrazekUzivatel"></a><br />
                        </td>
                        <td class="obsah">
                            <a href="/autor.jsp?id=<%=author.getId()%>"><span class="nick"><%=person.getNickName()%></span><span class="jmeno"><%=person.getFullName()%></span></a>
                            <div class="popisek"><%=person.getAge()%> let<br />
                                <%=person.getAddress()%></div>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </table>
            </div>

            <div style="clear: both"></div>


        </div>
    </div>


    <!-- PRAVE MENU -->

    <div class="pravaCast" id="skupina">
        <div class="praveMenu">
            <div class="nadpisMenu">O skupině</div>
        </div>

        <%
            if(loggedUser != null){
        %>
        <div class="sekce">
            <table>
                <tr>
                    <td>
                        <a href="/pridatHru.jsp"><img src="/img/icon/plus_icon.png" class="pridatIkona"></a>
                    </td>
                    <td class="pridatPopis">
                        <a href="/pridatHru.jsp">Přidat hru</a>
                    </td>
                </tr>
            </table>
        </div>
        <%
        } else {
        %>
        <div class="sekce">
            Pro přidání hry je potřeba se nejprve zaregistrovat.
        </div>
        <%
            }
        %>
    </div>

    <div style="clear: both"></div>


<%@include file="layout/footer.jsp" %>