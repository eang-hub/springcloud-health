package com.springhealth.intervention.client;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.springhealth.intervention.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    @Autowired
    OAuth2RestTemplate restTemplate;

    public User getUserByUserName(String userName){
    	
    	logger.debug("Get user: {}", userName);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 005e2a27-963d-471e-b1a3-c1b46aad038e");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
// 添加自定义请求头（例如，Authorization）
    	ResponseEntity<User> restExchange =
                restTemplate.exchange(
                        "http://zuulservice:5555/springhealth/user/users/username/{userName}",
                        HttpMethod.GET,
                        entity, User.class, userName);

        User user = restExchange.getBody();
        
        return user;
    }
}
