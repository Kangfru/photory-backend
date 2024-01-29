package com.ot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories(basePackages = "com.ot.repository")
public class OtApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtApplication.class, args);
    }

    @PostConstruct
    public void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
