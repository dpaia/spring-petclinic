package org.springframework.samples.petclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application. Implements Basic HTTP Authentication with
 * in-memory users.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Creates and configures the security filter chain. - Configures Basic HTTP
	 * Authentication - Secures endpoints based on HTTP method and role - Disables CSRF
	 * for stateless API - Configures stateless session management
	 * @param http the HttpSecurity to configure
	 * @return the configured SecurityFilterChain
	 * @throws Exception if an error occurs
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				// ADMIN role required for modifying operations
				.requestMatchers(HttpMethod.POST, "/**")
				.hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/**")
				.hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/**")
				.hasRole("ADMIN")
				// USER role required for read operations
				.requestMatchers(HttpMethod.GET, "/**")
				.hasRole("USER")
				// Any other request requires authentication
				.anyRequest()
				.authenticated())
			.httpBasic(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	/**
	 * Creates an in-memory user details service with predefined users. - Creates an admin
	 * user with ADMIN role (which includes USER role permissions) - Creates a regular
	 * user with USER role
	 * @return the configured InMemoryUserDetailsManager
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails admin = User.builder()
			.username("admin")
			.password(passwordEncoder().encode("adminpass"))
			.roles("ADMIN", "USER")
			.build();

		UserDetails user = User.builder()
			.username("user")
			.password(passwordEncoder().encode("userpass"))
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(admin, user);
	}

	/**
	 * Creates a BCrypt password encoder for secure password hashing.
	 * @return the configured password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
