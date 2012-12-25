<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%@include file="/layout/header.jsp" %>
<%@include file="/layout/menu.jsp" %>

<script type="text/javascript" src="/templates/js/game/search.js"></script>
<div id="content">

    <form>
        <input type="text" id="searchQuery"/>
        <label for="queryTarget">Oblast vyhledávání</label><select id="queryTarget" name="queryTarget">
        <option value="all">Vše</option>
        <option value="author">Autor</option>
        <option value="game">Název hry</option>
        <option value="description">Popis</option>
    </select>
        <input type="button" id="searchButton" value="Hledej"/>
    </form>
</div>

<%@include file="/layout/footer.jsp" %>