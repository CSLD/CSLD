package org.pilirion.url;

import java.util.ArrayList;
import java.util.List;

public class Translator {
    List<Route> routes;
    private String defaultRoute = "/index.jsp";

    Translator(){
        routes = new ArrayList<Route>();
    }

    public void addRoute(Route route){
        routes.add(route);
    }

    public void setDefaultRoute(String defaultRoute){
        this.defaultRoute = defaultRoute;
    }

    public String translate(String urlToTranslate){
        for(Route route: routes){
            if(urlToTranslate.startsWith(route.getBasePath())){
                return route.translate(urlToTranslate);
            }
        }
        return defaultRoute;
    }
}
