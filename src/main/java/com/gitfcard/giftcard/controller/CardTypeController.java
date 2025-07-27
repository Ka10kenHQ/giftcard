package com.gitfcard.giftcard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.GiftCardTypeResponseDTO;
import com.gitfcard.giftcard.service.CardTypeService;

@RestController
@RequestMapping("/api/card-types")
public class CardTypeController {

	private CardTypeService cardTypeService;

	@Autowired
	public CardTypeController(CardTypeService cardTypeService){
		this.cardTypeService = cardTypeService;
	}

	@GetMapping("")
	public List<GiftCardTypeResponseDTO> getAll(){
		return cardTypeService.getAll();
	}

}
