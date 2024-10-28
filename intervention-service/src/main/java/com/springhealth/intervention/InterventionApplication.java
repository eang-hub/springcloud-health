package com.springhealth.intervention;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

@SpringCloudApplication
@EnableBinding(Sink.class)
@EnableResourceServer
@EnableOAuth2Client
public class InterventionApplication {



	@Bean
	@LoadBalanced
	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext context,
												 OAuth2ProtectedResourceDetails details) {
		return new OAuth2RestTemplate(details, context);
	}
	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(redisConnectionFactory);

		return template;
	}
    public static void main(String[] args) {
        SpringApplication.run(InterventionApplication.class, args);
    }
}

