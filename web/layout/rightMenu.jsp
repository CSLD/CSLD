<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="rightcontent">
    <h2>right panel</h2>
    <% if(loggedUser != null) {
    %>
    <a href="/game/insert">Vložit hru</a>
    <a href="/author/insert">Vložit autora</a>
    <%
    }%>
</div>
