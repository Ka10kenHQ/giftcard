package com.gitfcard.giftcard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.gitfcard.giftcard.dto.ErrorResponseDTO;
import com.gitfcard.giftcard.dto.UserLoginDTO;
import com.gitfcard.giftcard.dto.UserRegisterDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserTokenResponseDTO;
import com.gitfcard.giftcard.exception.UserNotFoundException;
import com.gitfcard.giftcard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService){
		this.userService = userService;
	}

	@Operation(summary = "Login as user")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDTO user) {
		try {
			String token = userService.verifyUser(user);
			UserTokenResponseDTO tokenResponseDTO = new UserTokenResponseDTO(token);
			return ResponseEntity.ok(tokenResponseDTO);
		} catch (BadCredentialsException e) {
			ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid email or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
		} catch (UserNotFoundException e) {
			ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid email or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
		} catch (Exception e) {
			ErrorResponseDTO errorResponse = new ErrorResponseDTO("An error occurred during login");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}


	@Operation(summary = "Register a new user")
	@PostMapping("/register")
	public ResponseEntity<Object> register(@Valid @RequestBody UserRegisterDTO registerUser, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getFieldErrors().stream()
				.map(error -> error.getDefaultMessage())
				.reduce((msg1, msg2) -> msg1 + "; " + msg2)
				.orElse("Validation failed");
			ErrorResponseDTO errorResponse = new ErrorResponseDTO(errorMessage);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}

		try {
			UserResponceDTO userResponceDTO = userService.save(registerUser);
			return ResponseEntity.status(HttpStatus.CREATED).body(userResponceDTO);
		} catch (IllegalArgumentException e) {
			ErrorResponseDTO errorResponse = new ErrorResponseDTO("User already exists with this email");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
		} catch (Exception e) {
			ErrorResponseDTO errorResponse = new ErrorResponseDTO("An error occurred during registration");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

}
