package com.gitfcard.giftcard.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitfcard.giftcard.dto.UserLoginDTO;
import com.gitfcard.giftcard.dto.UserPatchDTO;
import com.gitfcard.giftcard.dto.UserRegisterDTO;
import com.gitfcard.giftcard.dto.UserRequestDTO;
import com.gitfcard.giftcard.dto.UserResponceDTO;
import com.gitfcard.giftcard.dto.UserUpdateDTO;
import com.gitfcard.giftcard.entity.Role;
import com.gitfcard.giftcard.entity.User;
import com.gitfcard.giftcard.exception.EmailAlreadyUsedException;
import com.gitfcard.giftcard.exception.UserNotFoundException;
import com.gitfcard.giftcard.repository.RoleRepository;
import com.gitfcard.giftcard.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@DependsOn("roleService")
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final JWTService jwtService;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authManager;



	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
		RoleRepository roleRepository, JWTService jwtService, AuthenticationManager authenticationManager){
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.jwtService = jwtService;
		this.authManager = authenticationManager;
	}


	public List<UserResponceDTO> getAllUsers(){
		return userRepository.findAll().stream()
		.map(UserResponceDTO::new)
		.collect(Collectors.toList());
	}

	public UserResponceDTO getUserById(Long id) throws UserNotFoundException {
		User user =  userRepository.findById(id)
		.orElseThrow(() -> new UserNotFoundException(id));
		return new UserResponceDTO(user);
	}

	public UserResponceDTO getUserByEmail(String email) throws UserNotFoundException{
		User user = userRepository.findByEmail(email)
		                          .orElseThrow(() -> new UserNotFoundException(email));
		return new UserResponceDTO(user);
	}


	public UserResponceDTO save(UserRequestDTO user) {

		String hashedPassword = passwordEncoder.encode(user.getPassword());

		User newUser = new User(user.getFirstName(), user.getLastName(), user.getEmail(), hashedPassword);
		
		Role userRole = roleRepository.findByRoleName("ROLE_USER")
		                .orElseThrow(() -> new RuntimeException("Default role not found."));

		newUser.setRoles(Set.of(userRole));

		User savedUser = userRepository.save(newUser);
		
		return new UserResponceDTO(savedUser);
	}

	public UserResponceDTO save(UserRegisterDTO user) {
		// Check if user already exists
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
		}

		String hashedPassword = passwordEncoder.encode(user.getPassword());

		User newUser = new User(user.getFirstName(), user.getLastName(), user.getEmail(), hashedPassword);
		
		Role userRole = roleRepository.findByRoleName("ROLE_USER")
		                .orElseThrow(() -> new RuntimeException("Default role not found."));

		newUser.setRoles(Set.of(userRole));

		User savedUser = userRepository.save(newUser);
		
		return new UserResponceDTO(savedUser);
	}

	public UserResponceDTO update(UserUpdateDTO user, Long id) throws Exception{
		User updatedUser = userRepository.findById(id)
		.orElseThrow(() -> new UserNotFoundException(id, user.getFirstName()));

		String hashedPassword = passwordEncoder.encode(user.getPassword());

		updatedUser.setFirstName(user.getFirstName());
		updatedUser.setLastName(user.getLastName());
		updatedUser.setEmail(user.getEmail());
		updatedUser.setPassword(hashedPassword);

		User storedUser = userRepository.save(updatedUser);

		return new UserResponceDTO(storedUser);
	}

	public UserResponceDTO updateByEmail(String email, UserUpdateDTO user) throws Exception {
		User updatedUser = userRepository.findByEmail(email)
							.orElseThrow(() -> new UserNotFoundException(email));

		if (user.getEmail() != null && !user.getEmail().equals(email)) {
			if (userRepository.existsByEmail(user.getEmail())) {
				throw new EmailAlreadyUsedException(user.getEmail());
			}
			updatedUser.setEmail(user.getEmail());
		}

		if (user.getFirstName() != null) {
			updatedUser.setFirstName(user.getFirstName());
		}
		if (user.getLastName() != null) {
			updatedUser.setLastName(user.getLastName());
		}

		if (user.getPassword() != null && !user.getPassword().isBlank()) {
			String hashedPassword = passwordEncoder.encode(user.getPassword());
			updatedUser.setPassword(hashedPassword);
		}

		User storedUser = userRepository.save(updatedUser);

		return new UserResponceDTO(storedUser);
	}

	public UserResponceDTO patchByEmail(String email, UserPatchDTO dto) throws Exception {
		User user = userRepository.findByEmail(email)
		.orElseThrow(() -> new UserNotFoundException(email));

		if (dto.getEmail() != null && !dto.getEmail().equals(email)) {
			if (userRepository.existsByEmail(dto.getEmail())) {
				throw new EmailAlreadyUsedException(dto.getEmail());
			}
			user.setEmail(dto.getEmail());
		}

		if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
		if (dto.getLastName() != null) user.setLastName(dto.getLastName());

		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			String hashedPassword = passwordEncoder.encode(dto.getPassword());
			user.setPassword(hashedPassword);
		}

		User updated = userRepository.save(user);
		return new UserResponceDTO(updated);
	}


	public void delete(Long id) throws Exception{
		User user = userRepository.findById(id)
		.orElseThrow(() -> new UserNotFoundException(id));
		userRepository.delete(user);
	}

	public String verifyUser(UserLoginDTO user) throws Exception{
		User dbUser = userRepository.findByEmail(user.getEmail())
		.orElseThrow(() -> new UserNotFoundException(user.getEmail()));

		Authentication authentication = authManager.authenticate(
			new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

		if (!authentication.isAuthenticated()) {
			throw new BadCredentialsException("Invalid username or password");
		}

		List<GrantedAuthority> authorities = dbUser.getRoles().stream()
	 	                                           .map(role -> new SimpleGrantedAuthority(role.getName())) 
		                                           .collect(Collectors.toList());
	
		return jwtService.generateToken(user.getEmail(), dbUser.getId(), authorities);

	}


	@PostConstruct
	public void createDefaultAdminUser() {
		String email = "admin@example.com";

		if (userRepository.findByEmail(email).isEmpty()) {
			Optional<Role> userRole = roleRepository.findByRoleName("ROLE_USER");
			Optional<Role> adminRole = roleRepository.findByRoleName("ROLE_ADMIN");

			if (userRole.isEmpty() || adminRole.isEmpty()) {
				throw new IllegalStateException("Roles must exist before creating the admin user!");
			}

			Set<Role> roles = new HashSet<>();
			roles.add(userRole.get());
			roles.add(adminRole.get());

			User admin = new User();
			admin.setEmail(email);
			admin.setFirstName("Mate");
			admin.setLastName("Kopaliani");
			admin.setPassword(passwordEncoder.encode("password"));
			admin.setRoles(roles);

			userRepository.save(admin);
			System.out.println("Admin user created.");
		} else {
			System.out.println("Admin user already exists.");
		}
	}

	
}
