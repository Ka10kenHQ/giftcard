package com.gitfcard.giftcard.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.entity.Role;
import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.exception.UserNotFoundException;
import com.gitfcard.giftcard.repository.RoleRepository;
import com.gitfcard.giftcard.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;


	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}


	public List<UserResponceDTO> getAllUsers(){
		return userRepository.findAll().stream()
		.map(UserResponceDTO::new)
		.collect(Collectors.toList());
	}

	public UserResponceDTO getUserById(Long id) throws Exception {
		User user =  userRepository.findById(id)
		.orElseThrow(() -> new UserNotFoundException(id));
		return new UserResponceDTO(user);
	}


	public UserResponceDTO save(UserRequestDTO user) {


		String hashedPassword = passwordEncoder.encode(user.getPassword());

		User newUser = new User(user.getFirstName(), user.getLastName(), user.getEmail(), hashedPassword);
		
		Role userRole = roleRepository.findByName("ROLE_USER")
		                .orElseThrow(() -> new RuntimeException("Default role not found."));

		newUser.setRoles(Set.of(userRole));

		User savedUser = userRepository.save(newUser);
		
		UserResponceDTO savedBody = new UserResponceDTO(savedUser);

		return savedBody;
	}

	public User update(UserUpdateDTO user, Long id) throws Exception{
		User updatedUser = userRepository.findById(id)
		.orElseThrow(() -> new UserNotFoundException(id, user.getFirstName()));

		String hashedPassword = passwordEncoder.encode(user.getPassword());

		updatedUser.setFirstName(user.getFirstName());
		updatedUser.setLastName(user.getLastName());
		updatedUser.setEmail(user.getEmail());
		updatedUser.setPassword(hashedPassword);

		return userRepository.save(updatedUser);
	}
	
}
