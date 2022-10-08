package cz.larpovadatabaze.graphql;

import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GraphQL request handler
 */
@RestController
public class GraphQLResource {
    private final GraphQLRequestExecutor executor;

    public GraphQLResource(GraphQLRequestExecutor executor) {
        this.executor = executor;
    }

    public void setWebApplication(WebApplication webApplication) {
        // Initialize in executor
        executor.setWebApplication(webApplication);
    }

    @PostMapping(path = "/graphql")
    public void postRequest(HttpServletRequest request, HttpServletResponse response) {
        executor.handlePostRequest(request, response);
    }

    @GetMapping(path="/graphql")
    @PutMapping(path="/graphql")
    public void notAllowed(HttpServletResponse response) {
        response.setStatus(405);
    }
}
