package com.gitfcard.giftcard.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailService(UserRepository userRepository){
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String email){
		User user = userRepository.findByEmail(email)
		.orElseThrow(() -> new EntityNotFoundException("User not found with this email: " + email));

		return new org.springframework.security.core.userdetails.User(
			user.getEmail(), user.getPassword(),
			user.getRoles().stream()
			.map(role -> new SimpleGrantedAuthority(role.getName()))
			.collect(Collectors.toList())
		);

	}


}
