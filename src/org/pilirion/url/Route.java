package org.pilirion.url;

import org.pilirion.utils.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * It represents one of the routes which the application contains. Every route means mapping of
 * the URL from /user/edit/:id/:text to /user/edit.jsp?id=1&text=gh
 *
 * @author Jakub Balhar
 * @version 1.0
 */
public class Route {
    private String newUrlCall;
    private String basePath;
    private List<Variable> variables;

    /**
     * @param defaultUrlExpression Expression which is expected.
     * @param newUrlCall New url to call as a path similar to /user/author.jsp It has to be valid path on server, which
     */
    public Route(String defaultUrlExpression, String newUrlCall){
        Url url = new Url(defaultUrlExpression);
        this.basePath = url.getBasePath();
        this.variables = new ArrayList<Variable>();

        this.newUrlCall = newUrlCall;
        Variable var;

        String[] urlParts = defaultUrlExpression.split("/");
        int actualPosition = 0;
        for(String urlPart: urlParts){
            if(urlPart.startsWith(":")){
                var = new Variable(urlPart.replaceAll(":", ""),actualPosition);
                variables.add(var);
            }
            actualPosition++;
        }
    }

    /**
     * @param urlToTranslate incoming url written by the user.
     * @return translated url on the server.
     */
    public String translate(String urlToTranslate){
        String newUrlCall = this.newUrlCall + "?";
        String[] urlParts = urlToTranslate.split("/");

        for(Variable var: variables){
            if(urlParts.length > var.getPosition()){
                newUrlCall += var.getName() + "=" + urlParts[var.getPosition()] + "&";
            }
        }
        newUrlCall = Strings.removeLast(newUrlCall);

        return newUrlCall;
    }

    public String getBasePath(){
        return basePath;
    }
}
