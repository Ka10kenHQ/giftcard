package com.gitfcard.giftcard.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	private User user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GiftCard> giftCards;


	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@Enumerated(EnumType.STRING)
	private OrderStatus status; // PENDING, COMPLETED, FAILED

	@Column(name = "total_price")
	private BigDecimal totalPrice;


	public Order(LocalDateTime orderDate, OrderStatus orderStatus, BigDecimal totalPrice){
		this.orderDate = orderDate;
		this.status = orderStatus;
		this.totalPrice = totalPrice;
	}

	public Order(){

	}


	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getOrderDate() {
		return this.orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public OrderStatus getStatus() {
		return this.status;
	}

	
	public List<GiftCard> getGiftCards() {
		return this.giftCards;
	}


	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setGiftCards(List<GiftCard> giftCards) {
		this.giftCards = giftCards;
	}

}
