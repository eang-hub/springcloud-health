package com.springhealth.intervention.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springhealth.intervention.domain.User;
import com.springhealth.intervention.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springhealth.intervention.domain.Intervention;
import com.springhealth.intervention.service.InterventionService;

@RestController
@RequestMapping(value="interventions")
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
	public User getUserByUserName(@PathVariable("userName") String userName,
									 @PathVariable("deviceCode") String deviceCode) {

		logger.info("Generate intervention for userName: {} and deviceCode: {}.", userName, deviceCode);

		// 使用编程式开发
		User user = userService.getUserByUserName(userName);

		return user;
	}
	
}
