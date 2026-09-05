package com.tinyurl.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tinyurl.service.UrlService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UrlService urlService;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Test
	void userShouldNotAccessAdminEndpoint() throws Exception {

		String token = createToken("user-123", "fazil", "USER");

		mockMvc.perform(delete("/api/admin/urls/url-1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());

		verifyNoInteractions(urlService);
	}

	@Test
	void adminShouldAccessAdminEndpoint() throws Exception {

		String token = createToken("admin-123", "admin", "ADMIN");

		mockMvc.perform(delete("/api/admin/urls/url-1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	void unauthenticatedUserShouldNotAccessAdminEndpoint() throws Exception {

		mockMvc.perform(delete("/api/admin/urls/url-1")).andExpect(status().isUnauthorized());
	}

	private String createToken(String userId, String username, String role) {

		SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

		Date now = new Date();

		return Jwts.builder().subject(username).claim("userId", userId).claim("role", role).issuedAt(now)
				.expiration(new Date(now.getTime() + 60 * 60 * 1000)).signWith(key).compact();
	}
}