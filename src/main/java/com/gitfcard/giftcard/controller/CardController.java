package com.gitfcard.giftcard.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.GiftCardResponseDTO;
import com.gitfcard.giftcard.dto.RedeemCardResponseDTO;
import com.gitfcard.giftcard.service.GiftCardService;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/cards")
public class CardController {
	private GiftCardService giftCardService;

	@Autowired
	public CardController( GiftCardService giftCardService){
		this.giftCardService = giftCardService;
	}

	@Operation(summary = "get all giftcards")
	@GetMapping("")
	public List<GiftCardResponseDTO> getAllCards(){
		return giftCardService.getAllCards();
	}

	@Operation(summary = "get giftcard by id")
	@GetMapping("/{id}")
	public GiftCardResponseDTO getCardById(@PathVariable UUID id) throws Exception{
		return giftCardService.getCardById(id);
	}

	@Operation(summary = "Redeem a gift card")
	@PostMapping("/{id}/redeem")
	public ResponseEntity<RedeemCardResponseDTO> redeemCard(@PathVariable UUID id) throws Exception {
		RedeemCardResponseDTO responseDTO = giftCardService.redeem(id);
		return ResponseEntity.ok(responseDTO);
	}

}
