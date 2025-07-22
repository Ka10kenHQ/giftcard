package com.gitfcard.giftcard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.service.GiftCardService;
import com.gitfcard.giftcard.service.UserService;


@RestController
@RequestMapping("/api/card")
public class CardController {
	private UserService userService;
	private GiftCardService giftCardService;

	public CardController(UserService userService, GiftCardService giftCardService){
		this.userService = userService;
		this.giftCardService = giftCardService;
	}



	@GetMapping("/{id}")
	public ResponseEntity<?> getCardById(@PathVariable Long id){
		return this.giftCardService.
	}

	
}
