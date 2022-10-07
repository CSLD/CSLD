package cz.larpovadatabaze;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.WebApplicationInitializer;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.apache.wicket.protocol.http.WicketFilter;

public class CsldWebApp implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(RootConfig.class);

        container.addListener(new ContextLoaderListener(rootContext));

        // Hibernate + Spring related
        container.addFilter("opensessioninview", OpenSessionInViewFilter.class)
            .addMappingForUrlPatterns(null, false, "/*");
        
        // Wicket related
        container.setInitParameter("applicationFactoryClassName", "org.apache.wicket.spring.SpringWebApplicationFactory");
        container.setInitParameter("applicationClassName", "cz.larpovadatabaze.Csld");
        container.setInitParameter("configuration", "deployment");
        container.addFilter("wicket-spring-hibernate", WicketFilter.class)
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.ERROR, DispatcherType.REQUEST), false, "/*");

        
    }
}