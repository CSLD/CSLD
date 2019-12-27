package cz.larpovadatabaze.calendar.component.panel;

import cz.larpovadatabaze.WithWicket;
import cz.larpovadatabaze.api.ValidatableForm;
import cz.larpovadatabaze.calendar.model.Event;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

/**
 * Tests creation of the event using standard panel. User must be logged in in order for this functionality to work.
 */
public class CreateEventPanelTest extends WithWicket {
    @Test
    public void createValidEventAsUser() {
        tester.startComponentInPage(new CreateEventPanel("id", Model.of(Event.getEmptyEvent())) {
        });

        tester.assertComponent("id:addEvent", ValidatableForm.class);

        FormTester createEventForm = tester.newFormTester("id:addEvent");

        createEventForm.setValue("name", "My First Event");
        tester.executeAjaxEvent("id:addEvent:name", "change");

        createEventForm.setValue("from", "12/05/2015");
        tester.executeAjaxEvent("id:addEvent:from", "change");

        createEventForm.setValue("to", "12/06/2015");
        tester.executeAjaxEvent("id:addEvent:to", "change");

        createEventForm.setValue("loc", "Praha");
        tester.executeAjaxEvent("id:addEvent:loc", "change");

        createEventForm.setValue("descriptionWrapper:description", "Short text describing the game.");
        tester.executeAjaxEvent("id:addEvent:descriptionWrapper:description", "change");
        tester.executeAjaxEvent("id:addEvent:descriptionWrapper:description", "input");

        createEventForm.setValue("labels:requiredLabels:0:label:checkbox", true);

        tester.executeAjaxEvent("id:addEvent:submit", "click");
    }
}
