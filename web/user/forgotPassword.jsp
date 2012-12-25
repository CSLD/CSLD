<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%@include file="/layout/header.jsp" %>
<%@include file="/layout/menu.jsp" %>

<div id="content">
    <div>
        <%
        if(request.getParameter("error") != null && !request.getParameter("error").equals("")){
        %>
        <span class="error">Chybný email.</span>
        <%
        }
        %>
    </div>
    <form method="post" action="/user/sendNewPwd.jsp">
        <input type="text" name="mail" />
        <input type="submit" value="Pošli nové heslo"/>
    </form>
</div>

<%@include file="/layout/footer.jsp" %>