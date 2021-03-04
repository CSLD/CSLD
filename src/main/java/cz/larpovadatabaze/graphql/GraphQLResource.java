package cz.larpovadatabaze.graphql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.larpovadatabaze.graphql.fetchers.UserMutationFetcherFactory;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.log4j.Logger;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.AbstractResource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
