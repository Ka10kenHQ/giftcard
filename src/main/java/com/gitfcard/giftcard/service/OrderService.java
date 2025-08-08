package com.gitfcard.giftcard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.OrderResponseDTO;
import com.gitfcard.giftcard.dto.RequestOrderDTO;
import com.gitfcard.giftcard.entity.GiftCard;
import com.gitfcard.giftcard.entity.GiftCardType;
import com.gitfcard.giftcard.entity.Order;
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
public class OrderService {

	private static final BigDecimal DEFAULT_GIFT_CARD_BALANCE = new BigDecimal("50.00");

	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private GiftCardRepository giftCardRepository;
	private GiftCardTypeRepository giftCardTypeRepository;

	@Autowired
	public OrderService(OrderRepository orderRepository, UserRepository userRepository,
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


		List<GiftCard> giftCards = new ArrayList<>();
		BigDecimal totalOrderPrice = BigDecimal.ZERO;
		LocalDateTime now = LocalDateTime.now();

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);


		for (RequestOrderDTO.GiftCardRequestDTO itemDTO : requestOrderDTO.getGiftCards()) {
			GiftCardType cardType = giftCardTypeRepository
											.findById(itemDTO.getCardTypeId())
											.orElseThrow(() -> new EntityNotFoundException("CardType not found: " + itemDTO.getCardTypeId()));

			for (int i = 0; i < itemDTO.getQuantity(); i++) {
				GiftCard giftCard = new GiftCard(
					generateRandomCode(),
					DEFAULT_GIFT_CARD_BALANCE,
					LocalDateTime.now().plusDays(365),
					cardType,
					order
				);
				giftCard.setRedeemed(false);
				giftCard.setCreationDate(now);
				giftCardRepository.save(giftCard);

				giftCards.add(giftCard);
				totalOrderPrice = totalOrderPrice.add(giftCard.getBalance());
			}
		}


		order.setGiftCards(giftCards);
		order.setTotalPrice(totalOrderPrice);

		orderRepository.save(order);

		List<OrderResponseDTO.GiftCardItemDTO> giftCardDTOs = giftCards.stream()
		.map(item -> new OrderResponseDTO.GiftCardItemDTO(
			             item.getId(),
			             item.getCode(),
			             item.getBalance(),
			             item.isRedeemed(),
			             item.getGiftCardType().getCurrency(),
			             item.getExpirationDate()
		                 ))
		            .toList();

		return new OrderResponseDTO(
			order.getId(),
			user.getId(),
			order.getOrderDate(),
			order.getStatus().name(),
			order.getTotalPrice(),
			giftCardDTOs
		);

	}


	public List<OrderResponseDTO> getOrderByUserEmail(String email) {
		List<Order> orders = orderRepository.findAllByUserEmail(email);

		return orders.stream().map(order -> {
			List<OrderResponseDTO.GiftCardItemDTO> giftCardItems = order.getGiftCards().stream()
			.map(card -> new OrderResponseDTO.GiftCardItemDTO(
				card.getId(),
				card.getCode(),
				card.getBalance(),
				card.isRedeemed(),
				card.getGiftCardType().getCurrency(),
				card.getExpirationDate()
			))
			.toList();

			return new OrderResponseDTO(
				order.getId(),
				order.getUser().getId(),
				order.getOrderDate(),
				order.getStatus().name(),
				order.getTotalPrice(),
				giftCardItems
			);
		}).toList();
	}

	public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
		List<Order> orders = orderRepository.findAllByUserId(userId);

		return orders.stream().map(order -> {
			List<OrderResponseDTO.GiftCardItemDTO> giftCardItems = order.getGiftCards().stream()
			.map(card -> new OrderResponseDTO.GiftCardItemDTO(
				card.getId(),
				card.getCode(),
				card.getBalance(),
				card.isRedeemed(),
				card.getGiftCardType().getCurrency(),
				card.getExpirationDate()
			))
			.collect(Collectors.toList());

			return new OrderResponseDTO(
				order.getId(),
				order.getUser().getId(),
				order.getOrderDate(),
				order.getStatus().name(),
				order.getTotalPrice(),
				giftCardItems
			);
		}).collect(Collectors.toList());
	}

	public OrderResponseDTO getOrderById(Long orderId) throws Exception {
		Order order = orderRepository.findById(orderId)
		.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

		List<OrderResponseDTO.GiftCardItemDTO> giftCardItems = order.getGiftCards().stream()
							  .map(card -> new OrderResponseDTO.GiftCardItemDTO(
									card.getId(),
									card.getCode(),
									card.getBalance(),
									card.isRedeemed(),
									card.getGiftCardType().getCurrency(),
									card.getExpirationDate()  // If you added expiration here
								))
								.collect(Collectors.toList());

		return new OrderResponseDTO(
			order.getId(),
			order.getUser().getId(),
			order.getOrderDate(),
			order.getStatus().name(),
			order.getTotalPrice(),
			giftCardItems
		);
	}


	private String generateRandomCode() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
	}



	
}
