package com.gitfcard.giftcard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

	@Id
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	public Role() {
		// Default constructor for JPA
	}

	public Role(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
