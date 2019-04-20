package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//import javafx.beans.value.WeakChangeListener;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

//    @Bean
//    public WebMvcConfigurer corsConfigure(){
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                super.addCorsMappings(registry);
////                registry
////                        .addMapping("/**")
////                        .allowedHeaders("*")
////                        .allowedOrigins("*")
////                        .allowedMethods("*");
//            }
//        };
//    }

}
