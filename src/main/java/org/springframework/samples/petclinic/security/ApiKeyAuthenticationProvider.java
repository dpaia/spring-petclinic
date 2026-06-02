package org.springframework.samples.petclinic.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication provider for API key based authentication. This provider validates API
 * keys against a predefined set of valid keys.
 */
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

	// In-memory map of API keys to client identifiers
	private final Map<String, String> apiKeys = new HashMap<>();

	public ApiKeyAuthenticationProvider() {
		apiKeys.put("test-api-key", "petclinic-client");
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		ApiKeyAuthenticationToken authToken = (ApiKeyAuthenticationToken) authentication;
		String apiKey = (String) authToken.getCredentials();

		// Check if the API key exists in our map
		if (apiKey == null || !apiKeys.containsKey(apiKey)) {
			throw new BadCredentialsException("Invalid API Key");
		}

		// Get the client identifier associated with this API key
		String clientId = apiKeys.get(apiKey);

		// Create a fully authenticated token with the ROLE_PARTNER authority
		return new ApiKeyAuthenticationToken(apiKey, clientId,
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTNER")));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * Adds a new API key to the in-memory store. This method is primarily for testing
	 * purposes.
	 * @param apiKey the API key to add
	 * @param clientId the client identifier associated with the API key
	 */
	public void addApiKey(String apiKey, String clientId) {
		apiKeys.put(apiKey, clientId);
	}

}
