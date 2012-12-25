(function csld(){
    // TODO password change only on submit. Not otherwise.
    String.prototype.isPwd = function isStringPwd(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0 && isOptional){
            return true;
        }
        if(this.length < 6){
            return false;
        }
        if(this.match(/[a-zA-Z0-9!#$%&'*+-/=?^_`{|}~(),:;<>@[\]]*/)){
            return true;
        }
        return false;
    };

    String.prototype.isMail = function isStringMail(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0 && isOptional){
            return true;
        }
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
        if(partsOfAddress[1].indexOf(".") == -1){
            return false;
        }
        return this.match(/[a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]*/);
    }

    String.prototype.isName = function isStringName(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0){
            if(isOptional){
                return true
            } else {
                return false;
            }
        }
        return this.match(/[a-zA-Z]*/);
    }

    String.prototype.isNumber = function isStringNumber(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0){
            if(isOptional){
                return true
            } else {
                return false;
            }
        }
        return this.match(/[0-9]*/);
    }

    String.prototype.isYear = function isStringYear(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0 && isOptional){
            return true;
        }
        if(this.length != 4){
            return false;
        }
        return this.match(/[0-9]*/);
    }

    String.prototype.isWeb = function isStringWeb(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0 && isOptional){
            return true;
        }
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

    String.prototype.isText = function isStringText(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0){
            if(isOptional){
                return true
            } else {
                return false;
            }
        }
        return true;
    }

    String.prototype.isDate = function isStringDate(isOptional){
        isOptional = (isOptional == undefined) ? false: isOptional;
        if(this.length == 0){
            if(isOptional){
                return true
            } else {
                return false;
            }
        }
        if(this.indexOf("-") != -1){
            var dateParts = this.split("-");
            if(dateParts.length != 3){
                return false;
            }
            if(dateParts[0].length != 4 && dateParts[2].length != 4){
                return false;
            }
            return true;
        }
        if(this.indexOf(".") != -1){
            var datePartsDot = this.split(".");
            if(datePartsDot.length != 3){
                return false;
            }
            if(datePartsDot[0].length != 4 && datePartsDot[2].length != 4){
                return false;
            }
            return true;
        }
        return false;
    }

    function testValue(type, value, field, errorElementId, isOptional){
        if(type == "pwd"){
            if(!value.isPwd(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "name"){
            if(!value.isName(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "year"){
            if(!value.isYear(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "web"){
            if(!value.isWeb(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "text"){
            if(!value.isText(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "mail"){
            if(!value.isMail(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "date"){
            if(!value.isDate(isOptional)){
                $(errorElementId).update(field);
                return false;
            } else {
                $(errorElementId).update("");
                return true;
            }
        } else if (type == "number"){
            if(!value.isNumber(isOptional)){
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
            var isOptional = false
            if(id.startsWith("o_")){
                isOptional = true;
            }
            var idParts = id.split("_");
            var type = idParts[idParts.length - 1];
            var value = (element.value == null) ? "" : element.value.toString();
            var errorElementId = idParts[0] + "Error";
            if(isOptional){
                errorElementId = idParts[1] + "Error";
            }
            var field = "V poli je chybná hodnota.";
            var isValid = testValue(type, value, field, errorElementId, isOptional);
            return isValid;
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
            var stringToTest = (element.value == null) ? "" : element.value.toString();
            var id = (element.id == null) ? "" : element.id;
            if(element.id == null){
                return true;
            }
            var isOptional = false
            if(id.startsWith("o_")){
                isOptional = true;
            }
            var idParts = id.split("_");
            var type = idParts[idParts.length - 1];
            var errorElementId = idParts[0] + "Error";
            if(isOptional){
                errorElementId = idParts[1] + "Error";
            }
            var field = "V poli je chybná hodnota.";
            return testValue(type, stringToTest, field, errorElementId, isOptional);
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
                var isFormValid = form.validate();
                if(!isFormValid){
                    Event.stop(event);
                    return false;
                }

                var allElements = form.getElements();
                allElements.each(function changePwdElements(element){
                    if(element.type == "password"){
                        element.value = element.value.md5();
                    }
                });
                return isFormValid;
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