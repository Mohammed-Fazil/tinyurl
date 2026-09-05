package com.tinyurl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlResponse;
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

		String userId = "user-123";

		when(counterService.getNextSequence()).thenReturn(123456L);

		when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		CreateUrlResponse response = urlService.createShortUrl(request, userId);

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

		String userId = "user-123";

		org.mockito.Mockito.doThrow(new InvalidUrlException("URL must start with http:// or https://"))
				.when(urlValidator).validate(request.url());

		// When & Then
		assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(request, userId));

		verify(urlValidator).validate(request.url());

		verifyNoInteractions(counterService);

		verifyNoInteractions(urlMappingRepository);
	}

	@Test
	void shouldReturnUserUrls() {

		// Given
		String userId = "user-123";

		UrlMapping url1 = UrlMapping.builder().id("id-1").sequenceId(123456L).shortCode("w7e")
				.originalUrl("https://google.com").userId(userId).createdAt(LocalDateTime.of(2026, 9, 5, 10, 30))
				.build();

		UrlMapping url2 = UrlMapping.builder().id("id-2").sequenceId(123457L).shortCode("w7f")
				.originalUrl("https://github.com").userId(userId).createdAt(LocalDateTime.of(2026, 9, 5, 11, 30))
				.build();

		when(urlMappingRepository.findByUserId(userId)).thenReturn(List.of(url1, url2));

		// When
		List<UrlResponse> responses = urlService.getUserUrls(userId);

		// Then
		assertEquals(2, responses.size());

		assertEquals("id-1", responses.get(0).id());
		assertEquals("w7e", responses.get(0).shortCode());
		assertEquals("https://google.com", responses.get(0).originalUrl());

		assertEquals("id-2", responses.get(1).id());
		assertEquals("w7f", responses.get(1).shortCode());
		assertEquals("https://github.com", responses.get(1).originalUrl());

		verify(urlMappingRepository).findByUserId(userId);
	}

	@Test
	void shouldDeleteUserOwnUrl() {

		String userId = "user-123";
		String urlId = "url-1";

		UrlMapping urlMapping = UrlMapping.builder().id(urlId).shortCode("w7e").originalUrl("https://google.com")
				.userId(userId).build();

		when(urlMappingRepository.findByIdAndUserId(urlId, userId)).thenReturn(Optional.of(urlMapping));

		urlService.deleteUrl(urlId, userId);

		verify(urlMappingRepository).findByIdAndUserId(urlId, userId);

		verify(urlMappingRepository).delete(urlMapping);
	}

	@Test
	void shouldNotDeleteUrlOwnedByAnotherUser() {

		String userId = "user-123";
		String urlId = "url-1";

		when(urlMappingRepository.findByIdAndUserId(urlId, userId)).thenReturn(Optional.empty());

		assertThrows(ShortUrlNotFoundException.class, () -> urlService.deleteUrl(urlId, userId));

		verify(urlMappingRepository).findByIdAndUserId(urlId, userId);

		verifyNoMoreInteractions(urlMappingRepository);
	}

	@Test
	void shouldDeleteAnyUrlAsAdmin() {

		String urlId = "url-1";

		UrlMapping urlMapping = UrlMapping.builder().id(urlId).shortCode("w7e").originalUrl("https://google.com")
				.userId("user-456").build();

		when(urlMappingRepository.findById(urlId)).thenReturn(Optional.of(urlMapping));

		urlService.deleteUrlAsAdmin(urlId);

		verify(urlMappingRepository).findById(urlId);

		verify(urlMappingRepository).delete(urlMapping);
	}

	@Test
	void shouldThrowExceptionWhenAdminDeletesUnknownUrl() {

		String urlId = "unknown";

		when(urlMappingRepository.findById(urlId)).thenReturn(Optional.empty());

		assertThrows(ShortUrlNotFoundException.class, () -> urlService.deleteUrlAsAdmin(urlId));

		verify(urlMappingRepository).findById(urlId);

		verifyNoMoreInteractions(urlMappingRepository);
	}
}