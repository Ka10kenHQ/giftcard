package com.gitfcard.giftcard.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gitfcard.giftcard.config.JwtAuthenticationFilter;
import com.gitfcard.giftcard.service.CustomUserDetailService;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private final CustomUserDetailService customUserDetailService;

	private final JwtAuthenticationFilter jwtAuthFilter;

	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	public SecurityConfig(CustomUserDetailService customUserDetailService,
		CustomAccessDeniedHandler customAccessDeniedHandler,
		JwtAuthenticationFilter jwtAuthFilter
	){
		this.customAccessDeniedHandler = customAccessDeniedHandler;
		this.customUserDetailService = customUserDetailService;
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	protected SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/api/auth/**").permitAll()  
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")  
				.anyRequest().authenticated()
			)
			.exceptionHandling(e -> e.accessDeniedHandler(customAccessDeniedHandler))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}


	@Bean
	protected AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}


	@Bean
	protected AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}

}
