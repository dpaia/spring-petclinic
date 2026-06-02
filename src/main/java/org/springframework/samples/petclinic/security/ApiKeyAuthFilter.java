package org.springframework.samples.petclinic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for API key based authentication. This filter extracts the API key from the
 * X-API-KEY header and authenticates the request.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

	private static final String API_KEY_HEADER = "X-API-KEY";

	private final AuthenticationManager authenticationManager;

	public ApiKeyAuthFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Extract API key from header
		String apiKey = request.getHeader(API_KEY_HEADER);

		// If no API key is provided or it's empty, return 401 Unauthorized
		if (apiKey == null || apiKey.isEmpty()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key is missing or empty");
			return;
		}

		try {
			// Create an authentication token with the API key
			ApiKeyAuthenticationToken authRequest = new ApiKeyAuthenticationToken(apiKey);

			// Attempt to authenticate the token
			Authentication authentication = authenticationManager.authenticate(authRequest);

			// If authentication is successful, set the authentication in the security
			// context
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Continue with the filter chain
			filterChain.doFilter(request, response);
		}
		catch (AuthenticationException e) {
			// If authentication fails, clear the security context and send 401
			// Unauthorized
			SecurityContextHolder.clearContext();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}

}
