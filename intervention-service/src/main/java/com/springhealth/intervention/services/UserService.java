package com.springhealth.intervention.services;

import com.springhealth.intervention.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public User getUserByUserName(String userName) {
        // 通过服务名获取所有注册的实例
        List<ServiceInstance> instances = discoveryClient.getInstances("userservice");

        // 如果没有找到服务实例，返回 null
        if (instances.isEmpty()) {
            return null;
        }

        // 获取第一个服务实例的 URI，并拼接用户服务的 URL
        String userserviceUri = String.format("%s/users/%s", instances.get(0).getUri(), userName);

        // 通过 RestTemplate 发送 GET 请求并获取结果
        ResponseEntity<User> response = restTemplate.exchange(userserviceUri, HttpMethod.GET, null, User.class, userName);

        // 返回 User 对象
        return response.getBody();
    }
}
