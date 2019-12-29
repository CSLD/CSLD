package cz.larpovadatabaze.common.api;

/**
 * Every listener to publisher must implement this.
 */
public interface IListener {
    public void notify(Object info);
}
