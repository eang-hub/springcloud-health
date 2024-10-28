package com.springhealth.intervention.service;

import com.springhealth.intervention.client.UserServiceClient;
import com.springhealth.intervention.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class UserService {
    @Autowired
    private UserServiceClient userClient;
    private final CircuitBreaker circuitBreaker;

    public UserService(CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.circuitBreaker = circuitBreakerFactory.create("userService");
    }


    public User getUserByUserName(String userName) {
        return userClient.getUserByUserName(userName);
    }
}
