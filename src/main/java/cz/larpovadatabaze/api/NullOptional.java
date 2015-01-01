package cz.larpovadatabaze.api;

/**
 * It throws Result doesn't exist Exception, if you try to retrieve the result and there is no such like.
 * It is possible to validate that the result doesn't exist.
 */
public class NullOptional<T> implements Option<T> {
    T result = null;

    public NullOptional(T result) {
        this.result = result;
    }

    public T getResult(){
        if(result != null) {
            return result;
        }  else {
            throw new NoResultException();
        }
    }

    public boolean isPresent() {
        return result != null;
    }
}
