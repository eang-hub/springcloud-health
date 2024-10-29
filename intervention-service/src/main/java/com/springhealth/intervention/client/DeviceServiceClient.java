package com.springhealth.intervention.client;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springhealth.intervention.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DeviceServiceClient {
	
    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceClient.class);


    // 更改隔离策略为信号量：可以将 Hystrix 的隔离策略改为信号量（SEMAPHORE）模式，从而避免线程切换带来的认证信息丢失
    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            },
            fallbackMethod = "fallback"
    )
    public DeviceMapper getDevice(String deviceCode){

    	logger.debug("Get device: {}", deviceCode);
        ResponseEntity<DeviceMapper> restExchange =
                restTemplate.exchange(
                        "http://zuulservice:5555/springhealth/device/devices/{deviceCode}",
                        HttpMethod.GET,
                        null, DeviceMapper.class, deviceCode);
         
        DeviceMapper device = restExchange.getBody();

        return device;
    }

    public DeviceMapper fallback(String userName, Throwable t) {
        DeviceMapper deviceMapper = new DeviceMapper();
        deviceMapper.setId(999999L);
        return deviceMapper;
    }

}