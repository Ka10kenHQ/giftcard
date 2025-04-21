package com.gitfcard.giftcard.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.UserLoginDTO;
import com.gitfcard.giftcard.service.UserService;

@RestController
@RequestMapping("/login")
public class LoginController {

	private final UserService userService;

	public LoginController(UserService userService){
		this.userService = userService;
	}

	@PostMapping
	public String login(@RequestBody  UserLoginDTO user) throws Exception{
		return userService.verifyUser(user);
	}

}
