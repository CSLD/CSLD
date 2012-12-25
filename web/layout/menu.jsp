<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="menu">
    <a href="/game/list">home</a>
    <a href="/game/list">databaze</a>
    <a href="/author/list">autori</a>
    <a href="/gallery/index">galerie</a>
    <a href="/contacts">kontakty</a>
    <%
        if(loggedUser != null){
    %>
    <a href="/user/detail">uživatel</a>
    <%
        } else {
    %>
    <a href="/user/register">registrace</a>
    <%
        }
    %>
</div>