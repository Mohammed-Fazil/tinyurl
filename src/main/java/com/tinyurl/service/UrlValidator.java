package com.tinyurl.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Component;

import com.tinyurl.exception.InvalidUrlException;

@Component
public class UrlValidator {

	public void validate(String url) {

		if (url == null || url.isBlank()) {
			throw new InvalidUrlException("URL cannot be empty");
		}

		try {
			URI uri = new URI(url);

			if (uri.getScheme() == null
					|| (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https"))) {

				throw new InvalidUrlException("URL must start with http:// or https://");
			}

			if (uri.getHost() == null || uri.getHost().isBlank()) {
				throw new InvalidUrlException("Invalid URL");
			}

		} catch (URISyntaxException e) {
			throw new InvalidUrlException("Invalid URL");
		}
	}
}