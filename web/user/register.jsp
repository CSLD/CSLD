<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%@include file="/layout/header.jsp" %>
<%@include file="/layout/menu.jsp" %>
<div id="content">
    <form method="post" action="/user/registerHandler.jsp" id="registerForm"  accept-charset="UTF-8">
        <a href="/user/forgotPass" >Zapomněl jsem heslo</a>
        <div name="formField">
            <span class="label">Uživatelské jméno: </span>
            <input type="text" id="regName_text" name="regName_text"/>
            <span id="regNameError" class="error"></span>
        </div>
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
        <div name="formField">
            <span class="label">Jméno: </span>
            <input type="text" id="regFirstName_name" name="regFirstName_name"/>
            <span id="regFirstNameError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Příjmení: </span>
            <input type="text" id="regLastName_name" name="regLastName_name"/>
            <span id="regLastNameError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Přezdívka: </span>
            <input type="text" id="regNickName_name" name="regNickName_name"/>
            <span id="regNickNameError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Email: </span>
            <input type="text" id="regMail_mail" name="regMail_mail"/>
            <span id="regMailError" class="error"></span>
        </div>
        <div name="formField">
            <span class="label">Datum narození (ve formátu den.měsíc.rok): </span>
            <input type="text" id="regBirthDate_date" name="regBirthDate_date"/>
            <span id="regBirthDateError" class="error"></span>
        </div>
        <div name="formField">
            <input type="submit" value="Registruj"/>
        </div>

    </form>

    <script type="text/javascript">
        document.observe("dom:loaded", function loginLoaded() {
            var nameIsFree = true;

            $('registerForm').observe('submit', function submitForm(event){
                if(!arePasswordsEqual(event)){
                    $('regPassword_pwd').value = "";
                    $('regPasswordCheck_pwd').value = "";
                    Event.stop(event);
                }
                if(!nameIsFree){
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

            function isUserNameFree(event){
                var requestParams = {
                    onSuccess: function(response){
                        if(response.responseJSON.free == "false"){
                            $('regNameError').update('Toto jméno je již zabrané.');
                            nameIsFree = false;
                        } else {
                            nameIsFree = true;
                        }
                    },

                    parameters: {
                        userName: event.target.value
                    }
                }

                new Ajax.Request("/user/isUserNameFreeAjax.jsp", requestParams);
            }

            $('regPassword_pwd').observe('change', arePasswordsEqual);
            $('regPasswordCheck_pwd').observe('change', arePasswordsEqual);
            $('regName_text').observe('change',isUserNameFree);
        })
    </script>
</div>
<%@include file="/layout/footer.jsp" %>