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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.protocol.http.WebApplication;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLRequestExecutor {
    private final static Logger logger = LogManager.getLogger();

    static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private final GraphQL graphQL;

    // Initialized later manually to overcome circular reference during bean creation
    private WebApplication webApplication;

    private final UserMutationFetcherFactory userFetcherFactory;
    private final SessionFactory sessionFactory;

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
    public GraphQLRequestExecutor(GraphQLTypeConfigurator typeConfigurator, UserMutationFetcherFactory userFetcherFactory, SessionFactory sessionFactory) {
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
        this.sessionFactory = sessionFactory;
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
    @Transactional
    public HttpServletResponse handlePostRequest(HttpServletRequest request, HttpServletResponse response) {
        ExecutionResult executionResult;

        restoreLoginWhenApplicable(request);

        try {
            // Parse and execute graphql request
            GraphQLRequest graphQLRequest = new Gson().fromJson(new InputStreamReader(request.getInputStream(), "UTF-8"), GraphQLRequest.class);

            if (graphQLRequest.query == null) {
                // Invalid request
                response.setStatus(400);
                return response;
            }

            ExecutionInput.Builder builder = ExecutionInput.newExecutionInput(graphQLRequest.query).operationName(graphQLRequest.operationName).variables(graphQLRequest.variables);
            executionResult = graphQL.execute(builder.build());
        }
        catch(Exception e) {
            logger.error("Error handling GraphQL request", e);
            response.setStatus(500);
            return response;
        }

        // Create response
        response.setContentType("application/json");
        try {
            response.getOutputStream().print(GSON.toJson(executionResult.toSpecification()));
        } catch (IOException e) {
            logger.error("Error outputting GraphQL request", e);
            response.setStatus(500);
        }

        return response;
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

}
