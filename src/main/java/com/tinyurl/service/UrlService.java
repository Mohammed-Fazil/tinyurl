package com.tinyurl.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlResponse;
import com.tinyurl.entity.UrlMapping;
import com.tinyurl.exception.ShortUrlNotFoundException;
import com.tinyurl.repository.UrlMappingRepository;

@Service
public class UrlService {

	@Value("${tinyurl.base-url}")
	private String baseUrl;

	private final UrlMappingRepository urlMappingRepository;
	private final CounterService counterService;
	private final Base62Encoder base62Encoder;
	private final UrlValidator urlValidator;

	public UrlService(UrlMappingRepository urlMappingRepository, CounterService counterService,
			Base62Encoder base62Encoder, UrlValidator urlValidator) {

		this.urlMappingRepository = urlMappingRepository;
		this.counterService = counterService;
		this.base62Encoder = base62Encoder;
		this.urlValidator = urlValidator;
	}

	public CreateUrlResponse createShortUrl(CreateUrlRequest request, String userId) {

		urlValidator.validate(request.url());

		long sequenceId = counterService.getNextSequence();

		String shortCode = base62Encoder.encode(sequenceId);

		UrlMapping urlMapping = UrlMapping.builder().sequenceId(sequenceId).shortCode(shortCode)
				.originalUrl(request.url()).userId(userId).createdAt(LocalDateTime.now()).build();

		urlMappingRepository.save(urlMapping);

		String shortUrl = baseUrl +"/"+ shortCode;

		return new CreateUrlResponse(shortUrl);
	}

	public String getOriginalUrl(String shortCode) {

		UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
				.orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

		return urlMapping.getOriginalUrl();
	}

	public List<UrlResponse> getUserUrls(String userId) {

		return urlMappingRepository.findByUserId(userId).stream()
				.map(urlMapping -> new UrlResponse(urlMapping.getId(), urlMapping.getShortCode(),
						urlMapping.getOriginalUrl(), baseUrl + "/" + urlMapping.getShortCode(),
						urlMapping.getCreatedAt()))
				.toList();

	}

	public void deleteUrl(String id, String userId) {

		Optional<UrlMapping> byId = urlMappingRepository.findById(id);

		Optional<UrlMapping> byIdAndUserId = urlMappingRepository.findByIdAndUserId(id, userId);

		UrlMapping urlMapping = byIdAndUserId.orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

		urlMappingRepository.delete(urlMapping);
	}

	public void deleteUrlAsAdmin(String id) {

		UrlMapping urlMapping = urlMappingRepository.findById(id)
				.orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

		urlMappingRepository.delete(urlMapping);
	}

}