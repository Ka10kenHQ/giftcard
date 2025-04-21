package com.gitfcard.giftcard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.gitfcard.giftcard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
	private final UserService userService;

	public AdminUserController(UserService userService){
		this.userService = userService;
	}

	@Operation(summary = "Get all users")
	@GetMapping
	public List<UserResponceDTO> getAllUser(){
		return userService.getAllUsers();
	}


	@Operation(summary = "Gey user by id")
	@GetMapping("/{id}")
	public UserResponceDTO getUserById(@PathVariable Long id) throws Exception{
		return userService.getUserById(id);
	}


	@Operation(summary = "add new user")
	@PostMapping
	public ResponseEntity<UserResponceDTO> addUser(@Valid  @RequestBody UserRequestDTO user){
		UserResponceDTO savedBody =  userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedBody);

	}

	@Operation(summary = "updated user info")
	@PutMapping("/{id}")
	public ResponseEntity<UserResponceDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO user) throws Exception {
		UserResponceDTO updatedUserBody = userService.update(user, id);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUserBody);
	}

	@Operation(summary = "delete user by id")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) throws Exception{
		userService.delete(id);
		return ResponseEntity.ok("User: " + id + " Deleted successfully");
	}
}
