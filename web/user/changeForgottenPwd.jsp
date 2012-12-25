<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<%@include file="/layout/header.jsp" %>
<%@include file="/layout/menu.jsp" %>

<%
    String userId = request.getParameter("userId");
    String key = request.getParameter("key");
    String sql = "select * from email_authentication where user_id = " + userId + " and mail_key = " + key;
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    boolean correct = false;
    while(rs.next()){
        correct = true;
    }
    if(!correct){
        response.sendRedirect("/game/list");
    }
%>

<div id="content">
    <form method="post" id="changePwd" action="/user/changePwd.jsp">
        <div name="formField">
            <span class="label">Heslo: </span>
            <input type="password" id="regPassword_pwd" name="regPassword_pwd"/>
            <span id="regPasswordError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Heslo pro kontrolu: </span>
            <input type="password" id="regPasswordCheck_pwd" name="regPasswordCheck_pwd"/>
            <span id="regPasswordCheckError" class="error"></span>
        </div>
        <input type="hidden" name="userId" value="<%=userId %>"/>
        <input type="hidden" name="key" value="<%=key %>"/>
        <input type="submit" value="Nastav heslo"/>
    </form>
</div>

<script type="text/javascript">
    document.observe("dom:loaded", function loginLoaded() {
        $('changePwd').observe('submit', function submitForm(event){
            if(!arePasswordsEqual(event)){
                $('regPassword_pwd').value = "";
                $('regPasswordCheck_pwd').value = "";
                Event.stop(event);
            }
        })

        function arePasswordsEqual(event){
            if($('regPassword_pwd').value != $('regPasswordCheck_pwd').value){
                $('regPasswordCheckError').update("Heslo a heslo pro kontrolu se liší.");
                return false;
            }
            return true;
        }


        $('regPassword_pwd').observe('change', arePasswordsEqual);
        $('regPasswordCheck_pwd').observe('change', arePasswordsEqual);
    })
</script>
<%@include file="/layout/footer.jsp" %>