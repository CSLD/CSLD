package cz.larpovadatabaze.graphql;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * GraphQL request handler
 */
@Component
public class GraphQLResource extends AbstractResource {
    private final GraphQLRequestExecutor executor;

    public GraphQLResource(GraphQLRequestExecutor executor) {
        this.executor = executor;
    }

    public void setWebApplication(WebApplication webApplication) {
        // Initialize in executor
        executor.setWebApplication(webApplication);
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        HttpServletRequest request = ((ServletWebRequest)attributes.getRequest()).getContainerRequest();
        if (request.getMethod().equals("POST")) {
            ResourceResponse response = executor.handlePostRequest(request);
            return response;
        }

        // Method not allowed
        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setStatusCode(405);
        return resourceResponse;
    }
}
