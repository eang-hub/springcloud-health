package com.springhealth.intervention.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springhealth.intervention.client.UserMapper;
import com.springhealth.intervention.domain.User;
import com.springhealth.intervention.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import com.springhealth.intervention.domain.Intervention;
import com.springhealth.intervention.service.InterventionService;

@RestController
@RequestMapping(value="interventions")
//@ConfigurationProperties(prefix = "springhealth.device.datacollect")
public class InterventionController {

    private static final Logger logger = LoggerFactory.getLogger(InterventionController.class);
    
    @Autowired
    private InterventionService interventionService;

	@Autowired
	private UserService userService;
	@RequestMapping(value = "/{userName}/{deviceCode}")
	public Intervention generateIntervention( @PathVariable("userName") String userName,
            @PathVariable("deviceCode") String deviceCode) {
		
		logger.info("Generate intervention for userName: {} and deviceCode: {}.", userName, deviceCode);
		
		Intervention intervention = interventionService.generateIntervention(userName, deviceCode);		
		
		return intervention;
	}


	@RequestMapping(value = "ccc/{userName}/{deviceCode}")
	public UserMapper getUserByUserName(@PathVariable("userName") String userName,
										@PathVariable("deviceCode") String deviceCode) {

		logger.info("Generate intervention for userName: {} and deviceCode: {}.", userName, deviceCode);

		// 使用编程式开发
		UserMapper user = userService.getUserByUserName(userName);

		return user;
	}


	@Value("${springhealth.device.datacollect.frequency}")
	private int frequency;
	@RequestMapping(value = "configServerValue")
	@ResponseBody
	public String getConfigServerValue() {

		return "从远程服务器获取到"+frequency;
	}
	
}
