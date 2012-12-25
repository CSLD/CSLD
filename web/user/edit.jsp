<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <%@include file="/layout/user/menuItems.jsp"%>
    <form method="post" action="/user/editHandler.jsp">
        <div id="user">
            <div name="formField">
                <span class="label">Uživatelské jméno: </span>
                <input type="text" id="o_userName_name" name="userName_name" value="<%=loggedUser.getUserName()%>" />
                <span id="userNameError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Heslo: </span>
                <input type="password" id="o_password_pwd" name="password_pwd" value="" />
                <span id="passwordError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Heslo pro kontrolu: </span>
                <input type="password" id="o_passwordCheck_pwd" value="" />
                <span id="passwordCheckError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Jméno: </span>
                <input type="text" id="o_firstName_name" name="firstName_name" value="<%=loggedUser.getPerson().getFirstName()%>" />
                <span id="firstNameError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Příjmení: </span>
                <input type="text" id="o_lastName_name" name="lastName_name" value="<%=loggedUser.getPerson().getLastName()%>" />
                <span id="lastNameError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Přezdívka: </span>
                <input type="text" id="o_nickName_name" name="nickName_name" value="<%=loggedUser.getPerson().getNickName()%>" />
                <span id="nickNameError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Datum narození (ve formátu den.měsíc.rok): </span>
                <input type="text" id="o_birthDate_date" name="birthDate_date" value="<%=loggedUser.getPerson().getBirthDate()%>" />
                <span id="birthDateError" class="error"></span>
            </div>
            <div name="formField">
                <span class="label">Email: </span>
                <input type="text" id="o_email_mail" name="email_mail" value="<%=loggedUser.getPerson().getEmail()%>" />
                <span id="emailError" class="error"></span>
            </div>
            <input type="submit" value="Edit" />
        </div>
    </form>
</div>
<%@include file="/layout/footer.jsp"%>