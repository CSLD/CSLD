package cz.larpovadatabaze.administration.rest;

import com.google.gson.Gson;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wicket.request.resource.AbstractResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class GCalSyncProducer extends AbstractResource {
    private final static Logger logger = LogManager.getLogger();;

    private final GoogleCalendarEvents googleCalendarEvents;

    public GCalSyncProducer(GoogleCalendarEvents googleCalendarEvents) {
        this.googleCalendarEvents = googleCalendarEvents;
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        HttpServletRequest request = ((HttpServletRequest)attributes.getRequest().getContainerRequest());
        String channelId = request.getHeader("X-Goog-Channel-ID");
        String channelExpiration = request.getHeader("X-Goog-Channel-Expiration");
        String messageNumber = request.getHeader("X-Goog-Message-Number");

        logger.info("Request for calendar sync, channelId=" + channelId + ", expiration=" + channelExpiration + ", messageNumber=" + messageNumber);

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
