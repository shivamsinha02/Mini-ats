package com.ats.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.cors-origins}") private String[] origins;

    @Override
    public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/api/**").allowedOrigins(origins).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }
}
