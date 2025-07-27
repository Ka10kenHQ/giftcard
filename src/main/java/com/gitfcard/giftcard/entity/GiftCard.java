package com.gitfcard.giftcard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gift_card")
public class GiftCard {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "code", unique = true, nullable = false)
	private String code;

	@Column(name = "balance", nullable = false)
	@Positive(message = "Balance must be positive")
	private BigDecimal balance;

	@Column(name = "expiration_date", nullable = false)
	@Future(message = "Expiration date must be in the future")
	private LocalDateTime expirationDate;

	@Column(name = "redeemed", nullable = false)
	private boolean redeemed;

	@Column(name = "creation_date", nullable = false)
	private LocalDateTime creationDate;


	@ManyToOne(optional = false)
	@JoinColumn(name = "gift_card_type_id")
	private GiftCardType giftCardType;

	public GiftCard(String code, BigDecimal balance, LocalDateTime expirationDate, GiftCardType giftCardType) {
		this.code = code;
		this.balance = balance;
		this.expirationDate = expirationDate;
		this.redeemed = false;
		this.creationDate = LocalDateTime.now();
		this.giftCardType = giftCardType;
	}


	public GiftCard(){

	}

	public UUID getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public boolean isRedeemed() {
		return redeemed;
	}

	public void redeem() {
		this.redeemed = true;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}


	public GiftCardType getGiftCardType() {
		return giftCardType;
	}


	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}


	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setRedeemed(boolean redeemed) {
		this.redeemed = redeemed;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setId(UUID id) {
		this.id = id;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public void setGiftCardType(GiftCardType giftCardType) {
		this.giftCardType = giftCardType;
	}
}
