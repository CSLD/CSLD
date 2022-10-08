package cz.larpovadatabaze.administration.rest;

import com.google.gson.Gson;
import cz.larpovadatabaze.calendar.service.GoogleCalendarEvents;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@RestController
public class GCalSyncProducer {
    private final static Logger logger = LogManager.getLogger();;

    private final GoogleCalendarEvents googleCalendarEvents;

    public GCalSyncProducer(GoogleCalendarEvents googleCalendarEvents) {
        this.googleCalendarEvents = googleCalendarEvents;
    }

    @GetMapping("/cal-sync")
    protected void newResourceResponse(HttpServletRequest request, HttpServletResponse response) {
        String channelId = request.getHeader("X-Goog-Channel-ID");
        String channelExpiration = request.getHeader("X-Goog-Channel-Expiration");
        String messageNumber = request.getHeader("X-Goog-Message-Number");

        logger.info("Request for calendar sync, channelId=" + channelId + ", expiration=" + channelExpiration + ", messageNumber=" + messageNumber);

        GoogleCalendarEvents.SyncStats stats = googleCalendarEvents.syncEventsFromGoogleCalendar();

        response.setContentType("application/json");

        String statisticsInJson = new Gson().toJson(stats);
        try {
            response.getOutputStream().print(statisticsInJson);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }
}
