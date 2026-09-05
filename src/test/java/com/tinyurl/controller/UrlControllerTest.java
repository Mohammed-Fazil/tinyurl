package com.tinyurl.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.exception.GlobalExceptionHandler;
import com.tinyurl.security.JwtAuthenticationToken;
import com.tinyurl.service.UrlService;

class UrlControllerTest {

	@Mock
	private UrlService urlService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		UrlController urlController = new UrlController(urlService);

		GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

		mockMvc = MockMvcBuilders.standaloneSetup(urlController).setControllerAdvice(exceptionHandler).build();
	}

	@Test
	void shouldCreateShortUrl() throws Exception {

		String userId = "user-123";
		String username = "fazil";

		when(urlService.createShortUrl(any(), eq(userId)))
				.thenReturn(new CreateUrlResponse("http://localhost:8080/w7e"));

		JwtAuthenticationToken authentication = new JwtAuthenticationToken("dummy-token", userId, username,
				List.of(new SimpleGrantedAuthority("ROLE_USER")));

		mockMvc.perform(post("/api/urls").principal(authentication).contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "url": "https://www.google.com"
				}
				""")).andExpect(status().isCreated()).andExpect(content().json("""
				{
				    "shortUrl": "http://localhost:8080/w7e"
				}
				"""));

		verify(urlService).createShortUrl(any(), eq(userId));
	}

	@Test
	void shouldRedirectToOriginalUrl() throws Exception {

		when(urlService.getOriginalUrl("w7e")).thenReturn("https://www.google.com");

		mockMvc.perform(get("/w7e")).andExpect(status().isFound())
				.andExpect(header().string("Location", "https://www.google.com"));

		verify(urlService).getOriginalUrl("w7e");
	}

	@Test
	void shouldReturnBadRequestForInvalidUrl() throws Exception {

		mockMvc.perform(post("/api/urls").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "url": "google.com"
				}
				""")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("URL must start with http:// or https://"));

		verifyNoInteractions(urlService);
	}
}