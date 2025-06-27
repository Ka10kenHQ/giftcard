package com.gitfcard.giftcard.entity;

import java.util.HashSet;
import java.util.Set;

import  jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

	@Id
	@Column(name = "role_name", unique = true, nullable = false)
	private String roleName;



	@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	private Set<User> users = new HashSet<>();

	public Role() {
		// Default constructor for JPA
	}

	public Role(String name){
		this.roleName = name;
	}

	public String getName(){
		return roleName;
	}
	
	public void setName(String name) {
		this.roleName = name;
	}
	
}
