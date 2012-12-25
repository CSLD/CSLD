package org.pilirion.url;

import org.pilirion.utils.Strings;

/**
 * It adds functionality to the Url.
 */
public class Url {
    String url;

    public Url(String url){
        this.url = url;
    }

    /**
     * It returns basePath of the url. Url can contain parts which starts with colons. Those parts represents variables
     * and therefore they are not part of the base part.
     *
     * @return part of url with stripped values.
     */
    public String getBasePath(){
        String[] urlParts = url.split("/");
        String baseUrl = "";
        for(String part: urlParts){
            if(part.startsWith(":")){
                break;
            }
            baseUrl += part + "/";
        }
        baseUrl = Strings.removeLast(baseUrl);
        return baseUrl;
    }

    public String getBasePathStripped() {
        String[] urlParts = url.split("/");
        String baseUrl = "";
        int indexStop;
        for(String part: urlParts){
            if(part.startsWith(":")){
                break;
            }
            indexStop = part.indexOf(".");
            if(indexStop != -1){
                part = part.substring(0, indexStop);
            }
            baseUrl += part + "/";
        }
        baseUrl = Strings.removeLast(baseUrl);
        return baseUrl;
    }
}
