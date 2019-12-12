package cz.larpovadatabaze.exceptions;

public class CsldRuntimeException extends RuntimeException {
    public CsldRuntimeException(Exception e) {
        super(e);
    }
}
