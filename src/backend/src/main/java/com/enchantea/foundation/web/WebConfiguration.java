package com.enchantea.foundation.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final String ALLOWED_ORIGIN;

    public WebConfiguration(@Value("${app.cors.allowed-origin:http://localhost:5173}") String ALLOWED_ORIGIN) {
        this.ALLOWED_ORIGIN = ALLOWED_ORIGIN;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(ALLOWED_ORIGIN)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type")
                .allowCredentials(true);
    }
}
