package cz.larpovadatabaze.api;

/**
 * Every listener to publisher must implement this.
 */
public interface IListener {
    public void notify(Object info);
}
