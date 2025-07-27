package com.gitfcard.giftcard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.RequestOrderDTO;
import com.gitfcard.giftcard.service.OderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private OderService orderService;


	public OrderController(OderService orderService){
		this.orderService = orderService;
	}

	@PostMapping("")
	public ResponseEntity<OrderResponseDTO> receiveOrder(@RequestBody @Valid RequestOrderDTO requestOrderDTO) {
		OrderResponseDTO response = orderService.processOrder(requestOrderDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
