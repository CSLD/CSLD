package org.pilirion.error;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 24.6.12
 * Time: 9:49
 */
public enum Errors {
    INCORRECT_USER_INFO("-1"),
    INCORRECT_GAME_INFO("-2");

    private String errorCode;
    Errors(String errorCode){
        this.errorCode = errorCode;
    }

    public String getCode(){
         return errorCode;
    }
}
