package org.springframework.samples.petclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application. This class configures the security filter
 * chain and authentication providers.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Configures the default security filter chain. This filter chain is applied to all
	 * other endpoints and allows public access.
	 */
	@Bean
	public SecurityFilterChain defaultFilterChain(HttpSecurity http, ApiKeyAuthFilter apiKeyAuthFilter)
			throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
			// Add the API key authentication filter before the username/password filter
			.addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configures the authentication manager with the API key authentication provider.
	 */
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http,
			ApiKeyAuthenticationProvider apiKeyAuthenticationProvider) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http
			.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.authenticationProvider(apiKeyAuthenticationProvider);
		return authenticationManagerBuilder.build();
	}

	@Bean
	public ApiKeyAuthFilter apiKeyAuthFilter(AuthenticationManager authenticationManager) {
		return new ApiKeyAuthFilter(authenticationManager);
	}

}
