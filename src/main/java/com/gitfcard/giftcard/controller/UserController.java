package com.gitfcard.giftcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.ErrorResponseDTO;
import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.UserPatchDTO;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.service.JWTUtil;
import com.gitfcard.giftcard.service.OrderService;
import com.gitfcard.giftcard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	private final OrderService orderService;
	private final JWTUtil jwtUtil;

	@Autowired
	public UserController(UserService userService,OrderService orderService, JWTUtil jwtUtil){
		this.userService = userService;
		this.orderService = orderService;
		this.jwtUtil = jwtUtil;
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


	@Operation(summary = "get current user info")
	@GetMapping("/me")
	public ResponseEntity<UserResponceDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
		if(userDetails == null){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = userDetails.getUsername();
		UserResponceDTO user = userService.getUserByEmail(email);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Change current user details")
	@PutMapping("/me")
	public ResponseEntity<?> updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserUpdateDTO userUpdateDTO) throws Exception {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = userDetails.getUsername();
		UserResponceDTO updatedUser = userService.updateByEmail(email, userUpdateDTO);

		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Change current user field")
	@PatchMapping("/me")
	public ResponseEntity<?> updateCurrentUserField(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserPatchDTO userUpdateDTO) throws Exception {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String email = userDetails.getUsername();
		UserResponceDTO updatedUser = userService.patchByEmail(email, userUpdateDTO);

		return ResponseEntity.ok(updatedUser);

	}

	@Operation(summary = "Get current users orders")
	@GetMapping("/me/orders")
	public ResponseEntity<List<OrderResponseDTO>> getCurrentUserOrders(@AuthenticationPrincipal UserDetails userDetails){
		String email = userDetails.getUsername();
		List<OrderResponseDTO> orders = orderService.getOrderByUserEmail(email);
		return ResponseEntity.ok(orders);
	}

	@Operation(summary = "Get users order by id")
	@GetMapping("/{id}/orders")
	public ResponseEntity<List<OrderResponseDTO>> getUsersOrdersById(@PathVariable Long id){
		List<OrderResponseDTO> orders = orderService.getOrdersByUserId(id);
		return ResponseEntity.ok(orders);
	}

}
