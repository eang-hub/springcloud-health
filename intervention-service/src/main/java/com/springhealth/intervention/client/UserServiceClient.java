package com.springhealth.intervention.client;

import com.springhealth.intervention.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    @Autowired
    RestTemplate restTemplate;

    public User getUserByUserName(String userName){

        //String apiUrl="http://zuulservice:5555/springhealth/user/users/{userName}";
        String apiUrl="http://gatewayserver:5555/springhealth/user/users/{userName}";

        ResponseEntity<User> restExchange =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.GET,
                        null, User.class, userName);

        User user = restExchange.getBody();

        return user;
    }
}
