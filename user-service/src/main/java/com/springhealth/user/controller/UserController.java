package com.springhealth.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springhealth.user.domain.User;
@RestController
@RequestMapping(value = "users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private HttpServletRequest request;

	@RequestMapping(value = "/{userName}", method = RequestMethod.GET)
	public User getUserByUserName(@PathVariable("userName") String userName) {

		logger.info("Get user by userName from port : {} of userservice instance", request.getServerPort());

		User user = new User();
		user.setId(001L);
		user.setUserCode("mockUser");
		user.setUserName(userName);
		return user;
	}
}
/**
 java -jar user-service-0.0.1-SNAPSHOT.jar --server.port=8082
 java -jar user-service-0.0.1-SNAPSHOT.jar --server.port=8083
 */