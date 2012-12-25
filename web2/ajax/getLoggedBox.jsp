<%@include file="/templates/header.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(loggedUser != null){
        Person person = loggedUser.getPerson();
%>
<div id="loginBoxPrihlaseny">
    <table>
        <tr>
            <td>
                <a href="uzivatel.jsp?id=<%=loggedUser.getId()%>"><img src="<%=person.getImage()%>"
                                                                       class="obrazekUzivatel"></a>
            </td>
            <td class="obsah">
                <a href="uzivatel.jsp?id=<%=loggedUser.getId()%>">
                    <div class="nick"><%=person.getNickName()%>
                    </div>
                    <div class="jmeno"><%=person.getFullName()%>
                    </div>
                </a>

                <div class="popisek"><a href="/registrace.jsp">Nastavení</a></div>
                <div class="popisek"><a href="/handlers/odhlasit.jsp">Odhlásit se</a></div>

            </td>
        </tr>
    </table>
</div>
<%
    }
%>