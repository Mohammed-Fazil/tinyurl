package com.tinyurl.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final JwtService jwtService;

	@Override
	public Authentication authenticate(Authentication authentication) {

		JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;

		String token = jwtAuthentication.getToken();

		// 1. Validate JWT
		if (!jwtService.isTokenValid(token)) {
			throw new BadCredentialsException("Invalid JWT token");
		}

		// 2. Extract information from JWT
		String userId = jwtService.extractUserId(token);

		String username = jwtService.extractUsername(token);

		String role = jwtService.extractRole(token);

		// 3. Convert role into Spring Security authority
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

		// 4. Create authenticated token
		return new JwtAuthenticationToken(token, userId, username, List.of(authority));
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}