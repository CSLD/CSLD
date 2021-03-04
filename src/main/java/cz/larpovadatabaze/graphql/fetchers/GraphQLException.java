package cz.larpovadatabaze.graphql.fetchers;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Our graphql exception with extended data
 */
public class GraphQLException extends RuntimeException implements GraphQLError {
    static enum ErrorCode {
        NOT_FOUND("NOT_FOUND"),
        INVALID_VALUE("INVALID_VALUE"),
        INVALID_STATE("INVALID_STATE"),
        ACCESS_DENIED("ACCESS_DENIED"),
        DUPLICATE_VALUE("DUPLICATE_VALUE");

        public final String code;

        ErrorCode(String code) {
            this.code = code;
        }
    }

    private final ErrorCode code;
    private final String path;
    private final String message;

    public GraphQLException(ErrorCode code, String message, String path) {
        super(message);
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public GraphQLException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.path = null;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("code", code.code);
        if (path != null) {
            map.put("path", path);
        }
        map.put("message", message);

        return map;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorPath() {
        return path;
    }
}
