package com.tinyurl.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.tinyurl.exception.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		ErrorResponse errorResponse = new ErrorResponse("Authentication required", LocalDateTime.now());

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}