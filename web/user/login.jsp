<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% if(loggedUser == null) {%>
<form method="post" action="/user/loginHandler.jsp" id="loginForm">
    <div name="formField">
        <span class="label">Uživatelské jméno: </span>
        <input type="text" id="loginName_text" name="loginName_text"/>
        <span id="loginNameError" class="error"></span>
    </div>
    <div name="formField">
        <span class="label">Heslo: </span>
        <input type="password" id="loginPassword_pwd" name="loginPassword_pwd"/>
        <span id="loginPasswordError" class="error"></span>
    </div>
    <input type="submit" value="Přihlásit se" />
</form>
<% } else {
%>
<div id="loggedUserName"><%=loggedUser.getUserName()%></div>
<a href="/user/logoutHandler.jsp">Odhlásit</a>
<%
}
%>