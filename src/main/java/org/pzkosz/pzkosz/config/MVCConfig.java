package org.pzkosz.pzkosz.config;

import org.pzkosz.pzkosz.middleware.InputValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Autowired
    private InputValidationInterceptor inputValidationInterceptor;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/search").setViewName("search");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/team/add").setViewName("team/add");
        registry.addViewController("/team/list").setViewName("team/list");
        registry.addViewController("/player/add").setViewName("player/add");
        registry.addViewController("/player/list").setViewName("player/list");
        registry.addViewController("/match/archive").setViewName("match/match-archive");
        registry.addViewController("/match/schedule").setViewName("match/match-schedule");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(inputValidationInterceptor).order(1);
    }

}
