package cz.larpovadatabaze.graphql.fetchers;

import graphql.cachecontrol.CacheControl;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.MergedField;
import graphql.execution.directives.QueryDirectives;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FragmentDefinition;
import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MockDataFetchingEnvironment implements DataFetchingEnvironment {
    private final Map<String, Object> arguments;
    private final Object source;

    public MockDataFetchingEnvironment(Map<String, Object> arguments, Object source) {
        this.arguments = arguments;
        this.source = source;
    }

    @Override
    public <T> T getSource() {
        return (T)source;
    }

    @Override
    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public boolean containsArgument(String s) {
        return arguments.containsKey(s);
    }

    @Override
    public <T> T getArgument(String s) {
        return (T)arguments.get(s);
    }

    @Override
    public <T> T getArgumentOrDefault(String s, T t) {
        return arguments.containsKey(s) ? getArgument(s) : t;
    }

    @Override
    public <T> T getContext() {
        return null;
    }

    @Override
    public <T> T getLocalContext() {
        return null;
    }

    @Override
    public <T> T getRoot() {
        return null;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return null;
    }

    @Override
    public List<Field> getFields() {
        return null;
    }

    @Override
    public MergedField getMergedField() {
        return null;
    }

    @Override
    public Field getField() {
        return null;
    }

    @Override
    public GraphQLOutputType getFieldType() {
        return null;
    }

    @Override
    public ExecutionStepInfo getExecutionStepInfo() {
        return null;
    }

    @Override
    public GraphQLType getParentType() {
        return null;
    }

    @Override
    public GraphQLSchema getGraphQLSchema() {
        return null;
    }

    @Override
    public Map<String, FragmentDefinition> getFragmentsByName() {
        return null;
    }

    @Override
    public ExecutionId getExecutionId() {
        return null;
    }

    @Override
    public DataFetchingFieldSelectionSet getSelectionSet() {
        return null;
    }

    @Override
    public QueryDirectives getQueryDirectives() {
        return null;
    }

    @Override
    public <K, V> DataLoader<K, V> getDataLoader(String s) {
        return null;
    }

    @Override
    public DataLoaderRegistry getDataLoaderRegistry() {
        return null;
    }

    @Override
    public CacheControl getCacheControl() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public OperationDefinition getOperationDefinition() {
        return null;
    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        return null;
    }
}
