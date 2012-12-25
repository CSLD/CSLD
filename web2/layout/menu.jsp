<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="menu">
    <span class="polozky">
        <span class="mezera"></span>
        <a href="/index.jsp">home</a>
        <a href="/zebricky.jsp">žebříčky</a>
        <a href="/autori.jsp">autoři</a>
        <a href="/uzivatele.jsp">uživatelé</a>
        <a href="/oDatabazi.jsp">o databázi</a>
    </span>
        <span id="divProHledani">
            <form method="get" action="/hledani.jsp">
                <input type="text" class="hledatBox" id="search_pwd" name="searchGame" title="Hledat">
                <a href="#" style="margin:0;"><input type="submit" class="glass" value=""></a>
            </form>
        </span>
</div>
<div style="margin-top: 20px;"></div>