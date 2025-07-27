package com.gitfcard.giftcard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gift_card_type")
public class GiftCardType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "currency")
	private String currency;

	public GiftCardType(String name, String currency){
		this.name = name;
		this.currency = currency;
	}

	public GiftCardType(){

	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCurrency() {
		return currency;
	}
	
}
