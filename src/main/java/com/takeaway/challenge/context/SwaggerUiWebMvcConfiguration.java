package com.takeaway.challenge.context;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Swagger UI Configuration
 */
public class SwaggerUiWebMvcConfiguration implements WebMvcConfigurer {

    private static final String RESOURCE_URL = "/swagger-ui/**";
    private static final String RESOURCE_LOCATION = "classpath:/META-INF/resources/webjars/springfox-swagger-ui/";
    private static final String FORWARD = "forward:";
    private static final String SWAGGER_UI = "/swagger-ui/";
    private static final String INDEX = "index.html";

    private final String baseUrl;

    public SwaggerUiWebMvcConfiguration(String baseUrl) {

        this.baseUrl = baseUrl;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');
        registry.
                addResourceHandler(baseUrl + RESOURCE_URL)
                .addResourceLocations(RESOURCE_LOCATION)
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController(baseUrl + SWAGGER_UI)
                .setViewName( FORWARD + baseUrl + SWAGGER_UI + INDEX);
    }
}