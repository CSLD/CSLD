package cz.larpovadatabaze.administration.rest;

import com.google.gson.Gson;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import org.apache.wicket.request.resource.AbstractResource;

import java.io.IOException;

public class GCalSyncProducer extends AbstractResource {
    private final GoogleCalendarEvents googleCalendarEvents;

    public GCalSyncProducer(GoogleCalendarEvents googleCalendarEvents) {
        this.googleCalendarEvents = googleCalendarEvents;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        GoogleCalendarEvents.SyncStats stats = googleCalendarEvents.syncEventsFromGoogleCalendar();

        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("application/json");
        resourceResponse.setTextEncoding("utf-8");

        String statisticsInJson = new Gson().toJson(stats);
        resourceResponse.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                attributes.getResponse().write(statisticsInJson);
            }
        });

        return resourceResponse;
    }
}
