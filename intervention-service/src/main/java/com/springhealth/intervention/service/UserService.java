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
        // 封装业务逻辑
        Supplier<User> userSupplier = () -> {
            return userClient.getUserByUserName(userName);
        };

        // 定义回退逻辑
        Function<Throwable, User> fallback = throwable -> {
            System.out.println("熔断触发，返回默认用户");
            return new User(0L, "no_user", "default_user");
        };

        // 执行逻辑，使用 Circuit Breaker 保护
        return circuitBreaker.run(userSupplier, fallback);
    }
}
