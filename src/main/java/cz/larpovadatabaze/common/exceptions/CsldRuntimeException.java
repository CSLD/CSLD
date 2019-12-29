package cz.larpovadatabaze.common.exceptions;

public class CsldRuntimeException extends RuntimeException {
    public CsldRuntimeException(Exception e) {
        super(e);
    }
}
