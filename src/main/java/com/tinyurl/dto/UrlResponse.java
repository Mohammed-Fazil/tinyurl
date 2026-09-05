package com.tinyurl.dto;

import java.time.LocalDateTime;

public record UrlResponse(String id, String shortCode, String originalUrl, String shortUrl, LocalDateTime createdAt) {
}