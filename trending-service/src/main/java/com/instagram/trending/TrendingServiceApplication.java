package com.instagram.trending;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrendingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrendingServiceApplication.class, args);
    }

}
