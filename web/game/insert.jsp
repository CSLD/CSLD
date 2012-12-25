<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.models.game.Type" %>
<%@ page import="org.pilirion.models.game.Types" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <form name="gameForm" action="insertHandler.jsp">
        <div name="formField">
            <span class="label">Jméno: </span>
            <input type="text" id="name_name" name="name_name" value="" />
            <span id="nameError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Rok: </span>
            <input type="text" id="year_year" name="year_year" value="" />
            <span id="yearError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Web: </span>
            <input type="text" id="web_web" name="web_web" value="" />
            <span id="webError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Popis: </span>
            <input type="text" id="description_text" name="description_text" value="" />
            <span id="descriptionError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Typ: </span>
            <select type="text" id="type" name="type">
                <%
                    Types tpTypes = new Types(conn);
                    List<Type> types = tpTypes.getAllTypes();
                    for(Type type: types){
                %>
                <option value="<%=type.getId()%>"><%=type.getName()%></option>
                <%
                    }
                %>
            </select>
            <span id="typeError" class="error"></span>
        </div>
        <%
            Authors aAuthors = new Authors(conn);
            List<Author> authors = aAuthors.getAllAuthors();
            if(authors != null){
        %>
        <div name="formField">
            <span class="label">Autoři: </span>
            <select type="text" id="authors" name="authors" multiple="multiple">
                <%
                    for(Author author: authors){
                %>
                <option value="<%=author.getId()%>"><%=author.getPerson().getNickName()%></option>
                <%
                    }
                %>
            </select>
            <span id="authorsError" class="error"></span>
        </div>
        <%
            }
        %>
        <input type="submit" value="Vlož" />
    </form>
</div>
<%@include file="/layout/footer.jsp"%>
