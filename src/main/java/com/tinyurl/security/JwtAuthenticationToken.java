package com.tinyurl.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final String token;
	private final String userId;
	private final String username;

	// Unauthenticated token
	public JwtAuthenticationToken(String token) {

		super(List.of());

		this.token = token;
		this.userId = null;
		this.username = null;

		setAuthenticated(false);
	}

	// Authenticated token
	public JwtAuthenticationToken(String token, String userId, String username,
			Collection<? extends GrantedAuthority> authorities) {

		super(authorities);

		this.token = token;
		this.userId = userId;
		this.username = username;

		setAuthenticated(true);
	}

	public String getToken() {
		return token;
	}

	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}
}