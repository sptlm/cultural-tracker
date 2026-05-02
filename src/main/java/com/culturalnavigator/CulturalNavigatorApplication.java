package com.culturalnavigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CulturalNavigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CulturalNavigatorApplication.class, args);
    }
}
