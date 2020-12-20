package cz.larpovadatabaze.graphql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.larpovadatabaze.graphql.fetchers.UserFetcherFactory;
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
    private final static Logger logger = Logger.getLogger(GraphQLResource.class);

    static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private final GraphQL graphQL;

    // Initialized later manually to overcome circular reference during bean creation
    private WebApplication webApplication;

    private final UserFetcherFactory userFetcherFactory;

    private class GraphQLRequest {
        private String operationName;
        private String query;
        private Map<String, Object> variables;

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }
    }

    @Autowired
    GraphQLResource(GraphQLTypeConfigurator typeConfigurator, UserFetcherFactory userFetcherFactory) {
        // Read schema definition from resource file
        ClassLoader classLoader = getClass().getClassLoader();
        BufferedReader schemaReader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("schema.graphql")));
        String schema = schemaReader.lines().collect(Collectors.joining());

        // Build schema from loaded definition
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        // Create type configuration
        RuntimeWiring runtimeWiring = typeConfigurator.configureTypes();

        // Create GraphQL instance
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        this.userFetcherFactory = userFetcherFactory;
    }

    public void setWebApplication(WebApplication webApplication) {
        this.webApplication = webApplication;

        // Also initialize user fetcher factory
        userFetcherFactory.setWebApplication(webApplication);
    }

    /**
     * Handle POST (graphql) request
     *
     * @param request HTTP request
     *
     * @return Response
     */
    private ResourceResponse handlePostRequest(HttpServletRequest request) {
        ResourceResponse resourceResponse = new ResourceResponse();
        ExecutionResult executionResult;

        restoreLoginWhenApplicable(request);

        try {
            // Parse and execute graphql request
            GraphQLRequest graphQLRequest = new Gson().fromJson(new InputStreamReader(request.getInputStream(), "UTF-8"), GraphQLRequest.class);

            if (graphQLRequest.query == null) {
                // Invalid request
                resourceResponse.setStatusCode(400);
                return resourceResponse;
            }

            ExecutionInput.Builder builder = ExecutionInput.newExecutionInput(graphQLRequest.query).operationName(graphQLRequest.operationName).variables(graphQLRequest.variables);
            executionResult = graphQL.execute(builder.build());
        }
        catch(Exception e) {
            logger.error("Error handling GraphQL request", e);
            resourceResponse.setStatusCode(500);
            return resourceResponse;
        }

        // Create response
        resourceResponse.setContentType("application/json");
        resourceResponse.setTextEncoding("utf-8");
        resourceResponse.setWriteCallback(new WriteCallback() {
            @Override
            public void writeData(Attributes attributes) throws IOException {
                attributes.getResponse().write(GSON.toJson(executionResult.toSpecification()));
            }
        });

        return resourceResponse;
    }

    /**
     * Attempt login the session when logged out and there is information for re-login stored
     */
    private void restoreLoginWhenApplicable(HttpServletRequest request) {
        AuthenticatedWebSession authenticatedWebSession = AuthenticatedWebSession.get();

        if (authenticatedWebSession.isSignedIn()) {
            // Signed in - OK
            return;
        }

        Cookie[] cookies = request.getCookies();
        boolean hasLoggedInCookie = cookies != null && Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().equals("LoggedIn"));
        if (!hasLoggedInCookie) {
            // Optimalization - check for cookie first - authentication strategy looks them more complicated way (?)
            return;
        }

        if (webApplication == null) {
            // Not set up
            return;
        }

        IAuthenticationStrategy authenticationStrategy = webApplication.getSecuritySettings().getAuthenticationStrategy();
        String[] stored = authenticationStrategy.load();
        if (stored == null || stored.length != 2) {
            // No auth stored
            return;
        }

        if (!authenticatedWebSession.signIn(stored[0], stored[1])) {
            // Cannot log in - remove stored info
            authenticationStrategy.remove();
        }

        // Logged in successfully
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
        HttpServletRequest request = ((ServletWebRequest)attributes.getRequest()).getContainerRequest();
        if (request.getMethod().equals("POST")) {
            return this.handlePostRequest(request);
        }

        // Method not allowed
        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setStatusCode(405);
        return resourceResponse;
    }
}
