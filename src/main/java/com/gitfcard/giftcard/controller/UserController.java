package com.gitfcard.giftcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.gitfcard.giftcard.dto.ErrorResponseDTO;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.service.GiftCardService;
import com.gitfcard.giftcard.service.JWTUtil;
import com.gitfcard.giftcard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	private final JWTUtil jwtUtil;
	private final GiftCardService giftCardService;

	@Autowired
	public UserController(UserService userService, JWTUtil jwtUtil, GiftCardService giftCardService){
		this.userService = userService;
		this.jwtUtil = jwtUtil;
		this.giftCardService = giftCardService;
	}

	@Operation(summary = "Get all users")
	@GetMapping("/")
	public List<UserResponceDTO> getAllUser(){
		return userService.getAllUsers();
	}


	@Operation(summary = "Get user by id")
	@GetMapping("/{id}")
	public UserResponceDTO getUserById(@PathVariable Long id) throws Exception{
		return userService.getUserById(id);
	}


	@Operation(summary = "Add new user")
	@PostMapping
	public ResponseEntity<UserResponceDTO> addUser(@Valid  @RequestBody UserRequestDTO user){
		UserResponceDTO savedBody =  userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedBody);

	}

	@Operation(summary = "Update user info")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO user, HttpServletRequest request) throws Exception {
		Long currentUserId = jwtUtil.getCurrentUserIdFromRequest(request);
		
		if (!id.equals(currentUserId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ErrorResponseDTO("You can only update your own profile"));
		}

		UserResponceDTO updatedUserBody = userService.update(user, id);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUserBody);
	}

	@Operation(summary = "Delete user by id")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) throws Exception{
		userService.delete(id);
		return ResponseEntity.ok("User: " + id + " Deleted successfully");
	}

	@Operation(summary = "Redeem the card")
	@GetMapping("/redeem/{id}")
	public ResponseEntity<String> redeemCard(@PathVariable Long id) throws Exception {
		giftCardService.redeem(id);
		return ResponseEntity.ok("Card successfully redeem for userId: " + id);
	}
}
