<%--
  Created by IntelliJ IDEA.
  User: Balda
  Date: 10.12.12
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (loggedUser == null) {
%>
<script type="text/javascript">
    $(document).ready(function(){
        $("#prihlaseniButton").click(function(event){
            $.ajax({
                url: "/handlers/prihlaseni.jsp",
                data: {
                    "e-mail": $("#e-mail").val(),
                    heslo: $("#heslo").val()
                },
                success: function(response){
                    response = JSON.parse(response.trim());
                    if(response.status == "ok"){
                        $.ajax({
                            url: "/ajax/getLoggedBox.jsp",
                            success: function(response){
                                var parent = $("#loginBox").parent();
                                $("#loginBox").remove();
                                parent.append(response);
                                location.reload();
                            }
                        });
                    } else {
                        alert(response.message);
                    }
                }
            });
            return false;
        });
    });
</script>
<div id="loginBox">

    <div class="overflowDiv">
        <div id="prihlaseniObalovaciVnitrni"><span id="odkazRozjizdeni">Přihlášení</span>

            <div id="prihlaseniObalovaci">
                <form action="/handlers/prihlaseni.jsp" method="post">
                    <input class="textbox" type="text" name="e-mail" id="e-mail" title="e-mail" value=""
                           style="width: 80px;">
                    <input class="textbox" type="password" name="heslo" id="heslo" title="heslo" value=""
                           style="width: 80px;">
                    <button class="prihlaseniButton" id="prihlaseniButton" type="submit" name="prihlasit"
                            title="Přihlásit">Přihlásit
                    </button>
                </form>
            </div>
        </div>
    </div>

    <script>
        var opened = false;
        $("#odkazRozjizdeni").click(function () {
            if (opened) {
                $("#prihlaseniObalovaciVnitrni").animate({top:'50px'});
                console.log("pri zavirani");
            } else {
                $("#prihlaseniObalovaciVnitrni").animate({top:'20px'});
                console.log("pri otevirani");
            }
            opened = opened ? false : true;
            return false;
        });
    </script>

    <a href="registrace.jsp">Registrace</a>
</div>
<%
} else {
    Person person = loggedUser.getPerson();
%>
<div id="loginBoxPrihlaseny">
    <table>
        <tr>
            <td>
                <a href="uzivatel.jsp?id=<%=loggedUser.getId()%>"><img src="<%=person.getImage()%>" class="obrazekUzivatel"></a>
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