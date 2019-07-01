package com.ibs.limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class LimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimiterApplication.class, args);
    }

}
