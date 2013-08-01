package cz.larpovadatabaze.api;

/**
 * Everz Component which can change status and wants to notify someone must implement this.
 */
public interface IPublisher {
    public void addListener(IListener listener);
}
