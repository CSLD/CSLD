package cz.larpovadatabaze.calendar.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {
    @GetMapping("/") 
    String return1(){ 
        return "Hello World"; 
    } 

    /*
     * 
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
     */
    
}
