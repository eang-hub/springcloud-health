package com.springhealth.user;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringCloudApplication
@EnableBinding(Source.class)
@EnableResourceServer
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
