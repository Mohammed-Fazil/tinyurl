package com.tinyurl.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tinyurl.security.JwtAuthenticationEntryPoint;
import com.tinyurl.security.JwtAuthenticationFilter;
import com.tinyurl.security.JwtAuthenticationProvider;
import com.tinyurl.security.JwtService;

import lombok.RequiredArgsConstructor;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;

	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider() {

		return new JwtAuthenticationProvider(jwtService);
	}

	@Bean
	public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider) {

		return new ProviderManager(List.of(jwtAuthenticationProvider));
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
			ObjectMapper objectMapper) throws Exception {

		JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint(objectMapper);

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,
				authenticationEntryPoint);

		http.csrf(csrf -> csrf.disable()).cors(cors -> {
		})

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))

				.authorizeHttpRequests(auth -> auth.requestMatchers("/{shortCode}").permitAll()

						.anyRequest().authenticated())

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}