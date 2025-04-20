package com.gitfcard.giftcard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Operations related to giftcard users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService){
		this.userService = userService;
	}

	@Operation(summary = "Get all users")
	@ApiResponse(responseCode = "200", description = "Users retrieved successfully")
	@GetMapping
	public List<UserResponceDTO> getAllUser(){
		return userService.getAllUsers();
	}


	@GetMapping("/{id}")
	public UserResponceDTO getUserById(@PathVariable Long id) throws Exception{
		return userService.getUserById(id);
	}


	@PostMapping
	public ResponseEntity<UserResponceDTO> addUser(@Valid  @RequestBody UserRequestDTO user){
		UserResponceDTO savedBody =  userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedBody);

	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponceDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO user) throws Exception {
		User updatedUser = userService.update(user, id);
		UserResponceDTO updatedUserBody = new UserResponceDTO(updatedUser);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUserBody);
	}
}
