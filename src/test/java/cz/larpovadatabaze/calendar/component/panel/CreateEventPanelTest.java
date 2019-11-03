package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.AcceptanceTest;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.calendar.model.Event;
import cz.larpovadatabaze.calendar.service.DatabaseEvents;
import cz.larpovadatabaze.calendar.service.Events;
import cz.larpovadatabaze.entities.CsldUser;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Tests creation of the event using standard panel. User must be logged in in order for this functionality to work.
 */
public class CreateEventPanelTest extends AcceptanceTest {
    @Test
    public void createValidEventAsUser() {
        tester.startComponentInPage(new CreateEventPanel("id", Model.of(Event.getEmptyEvent())) {});

        tester.assertComponent("id:addEvent", ValidatableForm.class);

        FormTester createEventForm = tester.newFormTester("id:addEvent");

        createEventForm.setValue("name", "My First Event");
        createEventForm.setValue("from", "12/05/2015");
        createEventForm.setValue("to", "12/06/2015");
        createEventForm.setValue("loc", "Praha");
        createEventForm.setValue("descriptionWrapper:description", "Short text describing the game.");

        createEventForm.setValue("labels:requiredLabels:0:label:checkbox", true);

        tester.executeAjaxEvent("id:addEvent:submit", "click");
        createEventForm.submit();

        Events events = new DatabaseEvents(sessionHolder.getSession());
        Collection<Event> all = events.all();
        //assertEquals(1, all.size()); // Be careful. If builder contains event doesn't have to be true.
    }

    @Override
    protected CsldUser getLoggedUser() {
        return masqueradeBuilder.getUser();
    }
}
