package org.springframework.samples.petclinic.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom authentication token for API key based authentication. This token is used to
 * represent an authentication request using an API key.
 */
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

	private final String apiKey;

	private final String principal;

	/**
	 * Creates an unauthenticated token with the provided API key. This constructor is
	 * used when the token is created from the request.
	 * @param apiKey the API key from the request
	 */
	public ApiKeyAuthenticationToken(String apiKey) {
		super(Collections.emptyList());
		this.apiKey = apiKey;
		this.principal = null;
		setAuthenticated(false);
	}

	/**
	 * Creates an authenticated token with the provided API key, principal, and
	 * authorities. This constructor is used when the token is authenticated by the
	 * provider.
	 * @param apiKey the API key from the request
	 * @param principal the principal (usually a client ID or name)
	 * @param authorities the granted authorities
	 */
	public ApiKeyAuthenticationToken(String apiKey, String principal,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.apiKey = apiKey;
		this.principal = principal;
		setAuthenticated(true);
	}

	/**
	 * Creates an authenticated token with the provided API key, principal, and a single
	 * authority. This is a convenience constructor for when only one authority is needed.
	 * @param apiKey the API key from the request
	 * @param principal the principal (usually a client ID or name)
	 * @param authority the authority to grant
	 */
	public ApiKeyAuthenticationToken(String apiKey, String principal, String authority) {
		super(Collections.singletonList(new SimpleGrantedAuthority(authority)));
		this.apiKey = apiKey;
		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return apiKey;
	}

	@Override
	public Object getPrincipal() {
		return principal != null ? principal : apiKey;
	}

}
