package com.tinyurl.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlResponse;
import com.tinyurl.security.JwtAuthenticationToken;
import com.tinyurl.service.UrlService;

import jakarta.validation.Valid;

@RestController

public class UrlController {

	private final UrlService urlService;

	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}

	@PostMapping("/api/urls")
	public ResponseEntity<CreateUrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request,
			Authentication authentication) {

		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

		String userId = jwtAuthenticationToken.getUserId();

		CreateUrlResponse response = urlService.createShortUrl(request, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{shortCode}")
	public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

		String originalUrl = urlService.getOriginalUrl(shortCode);

		return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
	}

	@GetMapping("/api/urls")
	public ResponseEntity<List<UrlResponse>> getUserUrls(Authentication authentication) {

		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

		String userId = jwtAuthenticationToken.getUserId();

		List<UrlResponse> urls = urlService.getUserUrls(userId);

		return ResponseEntity.ok(urls);
	}

	@DeleteMapping("/api/urls/{id}")
	public ResponseEntity<Void> deleteUrl(@PathVariable String id, Authentication authentication) {

		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

		String userId = jwtAuthenticationToken.getUserId();

		urlService.deleteUrl(id, userId);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/api/admin/urls/{id}")
	public ResponseEntity<Void> deleteUrlAsAdmin(@PathVariable String id) {

		urlService.deleteUrlAsAdmin(id);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/api/admin/users/{userId}/urls")
	public ResponseEntity<List<UrlResponse>> getUserUrlsAsAdmin(@PathVariable String userId) {

		List<UrlResponse> urls = urlService.getUserUrls(userId);

		return ResponseEntity.ok(urls);
	}
}