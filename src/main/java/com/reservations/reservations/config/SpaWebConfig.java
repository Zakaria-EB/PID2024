package com.reservations.reservations.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 1) Racine de l’app Vue
        registry.addViewController("/").setViewName("forward:/index.html");
        // 2) Toutes les routes sans extension (exclut .js, .css, .png…)
        registry.addViewController("/{spring:[^\\.]+}").setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:[^\\.]+}").setViewName("forward:/index.html");
    }
}