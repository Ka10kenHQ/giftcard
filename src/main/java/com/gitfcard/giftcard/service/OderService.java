package com.gitfcard.giftcard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.RequestOrderDTO;
import com.gitfcard.giftcard.entity.GiftCard;
import com.gitfcard.giftcard.entity.GiftCardType;
import com.gitfcard.giftcard.entity.Order;
import com.gitfcard.giftcard.entity.OrderItem;
import com.gitfcard.giftcard.entity.OrderStatus;
import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.repository.GiftCardRepository;
import com.gitfcard.giftcard.repository.GiftCardTypeRepository;
import com.gitfcard.giftcard.repository.OrderRepository;
import com.gitfcard.giftcard.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class OderService {

	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private GiftCardRepository giftCardRepository;
	private GiftCardTypeRepository giftCardTypeRepository;

	@Autowired
	public OderService(OrderRepository orderRepository, UserRepository userRepository,
		GiftCardRepository giftCardRepository,
		GiftCardTypeRepository giftCardTypeRepository){
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.giftCardTypeRepository = giftCardTypeRepository;
		this.giftCardRepository = giftCardRepository;
	}

	public OrderResponseDTO processOrder(@Valid RequestOrderDTO requestOrderDTO) {
		User user = userRepository.findById(requestOrderDTO.getUserId())
		                          .orElseThrow(() -> new EntityNotFoundException("User not found"));


		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal totalOrderPrice = BigDecimal.ZERO;

		for (RequestOrderDTO.OrderItemDTO itemDTO : requestOrderDTO.getOrderItems()) {
			GiftCardType cardType = giftCardTypeRepository
											.findById(itemDTO.getCardTypeId())
											.orElseThrow(() -> new EntityNotFoundException("CardType not found: " + itemDTO.getCardTypeId()));

			for (int i = 0; i < itemDTO.getQuantity(); i++) {
				GiftCard giftCard = new GiftCard(
					generateRandomCode(),
					new BigDecimal("50.00"),
					LocalDateTime.now().plusDays(365),
					cardType
				);
				giftCard.setRedeemed(false);
				giftCard.setCreationDate(LocalDateTime.now());
				giftCardRepository.save(giftCard);

				OrderItem orderItem = new OrderItem();
				orderItem.setGiftCard(giftCard);
				orderItems.add(orderItem);

				totalOrderPrice = totalOrderPrice.add(giftCard.getBalance());
			}
		}

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		order.setTotalPrice(totalOrderPrice);
		order.setOrderItems(orderItems);

		orderItems.forEach(item -> item.setOrder(order)); 
		orderRepository.save(order);

		List<OrderResponseDTO.GiftCardItemDTO> giftCardDTOs = orderItems.stream()
		.map(item -> {
			GiftCard gc = item.getGiftCard();
			GiftCardType type = gc.getGiftCardType();

			return new OrderResponseDTO.GiftCardItemDTO(
				gc.getId(),
				gc.getCode(),
				gc.getBalance(),
				gc.isRedeemed(),
				type.getCurrency()
			);
		}).toList();

		return new OrderResponseDTO(
			order.getId(),
			user.getId(),
			order.getOrderDate(),
			order.getStatus().name(),
			order.getTotalPrice(),
			giftCardDTOs
		);

	}

	private String generateRandomCode() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
	}



	
}
