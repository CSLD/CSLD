package cz.larpovadatabaze.api;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Everz Component which can change status and wants to notify someone must implement this.
 */
public interface IPublisher {
    public void addListener(IListener listener);
}
