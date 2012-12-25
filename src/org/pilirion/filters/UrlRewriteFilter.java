package org.pilirion.filters;

import org.pilirion.url.CsldTranslator;
import org.pilirion.url.Translator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 19.6.12
 * Time: 11:34
 */
public class UrlRewriteFilter implements Filter {
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest=(HttpServletRequest)servletRequest;

        String incomingUrl=httpRequest.getServletPath();

        if(incomingUrl.indexOf('.') == -1){
            Translator translator = CsldTranslator.getTranslator();
            String newUrl = translator.translate(incomingUrl);

            RequestDispatcher request_Dispatcher=servletRequest.getRequestDispatcher(newUrl);
            request_Dispatcher.forward(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
