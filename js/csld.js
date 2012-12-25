(function csld(){
    String.prototype.isPwd = function isStringPwd(){
        if(this.length < 6){
            return false;
        }
        return this.match(/[a-zA-Z0-9!#$%&'*+-/=?^_`{|}~(),:;<>@[\]]*/);
    };

    String.prototype.isMail = function isStringMail(){
        if(this.indexOf("@") == -1){
            return false;
        }
        if(this.indexOf(".") == 0 || this.indexOf(".") == (this.length - 1)){
            return false;
        }
        var partsOfAddress = this.split("@");
        if(partsOfAddress.length > 2){
            return false;
        }
        if(partsOfAddress.indexOf(".") == -1){
            return false;
        }
        return this.match(/[a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*/);
    }

    String.prototype.isName = function isStringName(){
        if(this.length == 0){
            return false;
        }
        return this.match(/[a-zA-Z]*/);
    }

    String.prototype.isNumber = function isStringNumber(){
        if(this.length == 0){
            return false;
        }
        return this.match(/[0-9]*/);
    }

    String.prototype.isYear = function isStringYear(){
        if(this.length != 4){
            return false;
        }
        return this.match(/[0-9]*/);
    }

    String.prototype.isWeb = function isStringWeb(){
        if(this.length == 0 || this.length > 255){
            return false;
        }
        var webParts = this.split(".");
        if(webParts.length < 3){
            return false;
        }
        if(webParts[0] != "www"){
            return false;
        }
        return webParts[1].match(/[0-9a-zA-Z]*/);
    }

    String.prototype.isText = function isStringText(){
        if(this.length == 0){
            return false;
        }
        return true;
    }

    String.prototype.isDate = function isStringDate(){
        if(this.length == 0){
            return false;
        }
        return this.match(/[0-9]*/);
    }

    function testValue(type, value, field, errorElementId){
        if(type == "pwd"){
            if(!value.isPwd()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "name"){
            if(!value.isName()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "year"){
            if(!value.isYear()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "web"){
            if(!value.isWeb()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "text"){
            if(!value.isText()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "mail"){
            if(!value.isMail()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "date"){
            if(!value.isDate()){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else {
            return true;
        }
    }

    Element.addMethods("INPUT", {
        // pwd, name, mail, date
        validate: function validateInput(element){
            var element = $(element);
            if(element.type == "button" || element.type == "submit" || element.type == "checkbox"){
                return true;
            }
            var id = (element.id == null) ? "" : element.id;
            if(element.id == null){
                return true;
            }
            var idParts = id.split("_");
            var type = idParts[idParts.length - 1];
            var value = (element.value == null) ? "" : element.value.toString();
            var errorElementId = idParts[0] + "Error";
            var field = "V poli je chybná hodnota.";
            return testValue(type, value, field, errorElementId);
        }
    });

    Element.addMethods("SELECT", {
        validate: function validateSelect(element){
            return true;
        }
    });

    Element.addMethods("TEXTAREA", {
        validate: function validateTextarea(element){
            var element = $(element);
            var stringToTest = (element.innerHTML == null) ? "" : element.innerHTML.toString();
            var id = (element.id == null) ? "" : element.id;
            if(element.id == null){
                return true;
            }
            var idParts = id.split("_");
            var type = idParts[idParts.length - 1];
            var errorElementId = idParts[0] + "Error";
            var field = "V poli je chybná hodnota.";
            return testValue(type, stringToTest, field, errorElementId);
        }
    });

    Element.addMethods("FORM", {
        validate: function validateForm(element){
            var element = $(element);
            var formElements = element.getElements();
            var isValid = true;
            formElements.each(function validateFormElement(formElement){
                if(!formElement.validate()){
                    isValid = false;
                }
            });
            return isValid;
        }
    });

    document.observe("dom:loaded", function loadedFunction(){
        $$('form').each(function everyFormEvent(form){
            form.observe('submit', function onFormSubmit(event){
                return form.validate();
            });

            var allElements = form.getElements();
            allElements.each(function everyFormElement(formElement){
                formElement.observe('change', function changeFormElement(event){
                    return event.target.validate();
                });
            });
        });
    })
})();