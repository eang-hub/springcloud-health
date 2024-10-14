package com.springhealth.intervention.controller;

import com.springhealth.intervention.client.UserMapper;
import com.springhealth.intervention.client.UserServiceClient;
import com.springhealth.intervention.domain.User;
import com.springhealth.intervention.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "users")
public class UserController {

	@Autowired
	private UserService userService;


	@Autowired
	private UserServiceClient userServiceClient;


	@GetMapping("userName/{userName}")
	public ResponseEntity<User> getUser(@PathVariable String userName) {
		// 使用 UserService 获取用户信息
		User user = userServiceClient.getUserByUserName(userName);

		// 如果用户信息为空，返回 404 NOT FOUND
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// 返回用户信息
		return ResponseEntity.ok(user);
	}

	@GetMapping("name/{userName}")
	public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
		// 使用 UserService 获取用户信息
		User user = userService.getUserByUserName(userName);

		// 如果用户信息为空，返回 404 NOT FOUND
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// 返回用户信息
		return ResponseEntity.ok(user);
	}
}
