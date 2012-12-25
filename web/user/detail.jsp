<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <%@include file="/layout/user/menuItems.jsp"%>
    <div id="user">
        <div id="userName"><%=loggedUser.getUserName()%></div>
        <div id="firstName"><%=loggedUser.getPerson().getFirstName()%></div>
        <div id="lastName"><%=loggedUser.getPerson().getLastName()%></div>
        <div id="nickName"><%=loggedUser.getPerson().getNickName()%></div>
        <div id="birthDate"><%=loggedUser.getPerson().getBirthDate()%></div>
        <div id="email"><%=loggedUser.getPerson().getEmail()%></div>
        <a href="/user/edit">Editovat</a>
    </div>
</div>
<%@include file="/layout/footer.jsp"%>