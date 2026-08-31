package com.tinyurl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.entity.UrlMapping;
import com.tinyurl.exception.InvalidUrlException;
import com.tinyurl.exception.ShortUrlNotFoundException;
import com.tinyurl.repository.UrlMappingRepository;

class UrlServiceTest {

	@Mock
	private UrlMappingRepository urlMappingRepository;

	@Mock
	private CounterService counterService;

	@Mock
	private UrlValidator urlValidator;

	private Base62Encoder base62Encoder;

	private UrlService urlService;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		base62Encoder = new Base62Encoder();

		urlService = new UrlService(urlMappingRepository, counterService, base62Encoder, urlValidator);
	}

	@Test
	void shouldCreateShortUrl() {

		// Given
		CreateUrlRequest request = new CreateUrlRequest("https://example.com");

		when(counterService.getNextSequence()).thenReturn(123456L);

		when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		CreateUrlResponse response = urlService.createShortUrl(request);

		// Then
		assertEquals("http://localhost:8080/w7e", response.shortUrl());

		verify(urlValidator).validate(request.url());

		verify(counterService).getNextSequence();

		verify(urlMappingRepository).save(any(UrlMapping.class));
	}

	@Test
	void shouldReturnOriginalUrl() {

		// Given
		String shortCode = "w7e";

		UrlMapping urlMapping = UrlMapping.builder().sequenceId(123456L).shortCode(shortCode)
				.originalUrl("https://www.google.com").build();

		when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.of(urlMapping));

		// When
		String originalUrl = urlService.getOriginalUrl(shortCode);

		// Then
		assertEquals("https://www.google.com", originalUrl);

		verify(urlMappingRepository).findByShortCode(shortCode);
	}

	@Test
	void shouldThrowExceptionWhenShortCodeNotFound() {

		// Given
		String shortCode = "unknown";

		when(urlMappingRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

		// When & Then
		ShortUrlNotFoundException exception = assertThrows(ShortUrlNotFoundException.class,
				() -> urlService.getOriginalUrl(shortCode));
		assertEquals("Short URL not found", exception.getMessage());

		verify(urlMappingRepository).findByShortCode(shortCode);
	}

	@Test
	void shouldNotCreateShortUrlForInvalidUrl() {

		// Given
		CreateUrlRequest request = new CreateUrlRequest("google.com");

		Mockito.doThrow(new InvalidUrlException("URL must start with http:// or https://")).when(urlValidator)
				.validate(request.url());

		// When & Then
		assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(request));

		verify(urlValidator).validate(request.url());

		Mockito.verifyNoInteractions(counterService);

		Mockito.verifyNoInteractions(urlMappingRepository);
	}
}