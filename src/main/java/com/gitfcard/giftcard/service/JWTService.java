package com.gitfcard.giftcard.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

	private String secretKey = "";

	private Long expirationTime = 600000L;

	
	public JWTService() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}


	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	public boolean validateToken(String token) {
		return !isTokenExpired(token);
	}

	public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
		Claims claims = extractAllClaims(token);
		List<String> roles = claims.get("roles", List.class);
		return roles.stream()
		.map(SimpleGrantedAuthority::new)
		.collect(Collectors.toList());
	}


	public String generateToken(String userEmail, Collection<? extends GrantedAuthority> authorities) {
		Date issuedAt = new Date(System.currentTimeMillis());
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

		Map<String, Object> claims = new HashMap<>();

		claims.put("roles", authorities.stream()
			                       .map(GrantedAuthority::getAuthority)
			                       .collect(Collectors.toList()));

		return Jwts.builder()
		.claims()
		.add(claims)
		.subject(userEmail)
		.issuedAt(issuedAt)
		.expiration(expirationDate)
		.and()
		.signWith(getKey())
		.compact();
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser()
		.verifyWith(getKey())
		.build()
		.parseSignedClaims(token)
		.getPayload();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}


	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}

