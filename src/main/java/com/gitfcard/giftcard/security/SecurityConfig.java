package com.gitfcard.giftcard.security;


import org.springframework.cglib.core.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebSecurity
@Configuration
@EnableWebMvc
public class SecurityConfig {



	@Bean
	protected UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		manager.createUser(
			User.builder()
			.username("user")
			.password(passwordEncoder.encode("password"))
			.roles("USER")
			.build()
		);

		manager.createUser(
			User.builder()
			.username("admin")
			.password(passwordEncoder.encode("password"))
			.roles("USER", "ADMIN")
			.build()
		);

		return manager;
	}

	@Bean
	@Order(1)                                                        
	protected SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/user/**").hasRole("USER")
				.anyRequest().authenticated()
			)
				.oauth2ResourceServer((oauth2) -> oauth2
				.jwt(jwt -> jwt
					.jwtAuthenticationConverter(jwtAuthenticationConverter())
				)
			);
		return http.build();
	}

	static class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
		public AbstractAuthenticationToken convert(Jwt jwt) {
			return new CustomAuthenticationToken(jwt);
		}
	}

	@Bean
	protected JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}


	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
