package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the security configuration. Tests access to endpoints based on
 * user roles and HTTP methods.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import({ SecurityConfigTests.TestConfig.class, SecurityConfigTests.TestController.class })
public class SecurityConfigTests {

	@Autowired
	private MockMvc mockMvc;

	private String createBasicAuthHeader(String username, String password) {
		String auth = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
	}

	@Test
	public void testGetEndpointWithUserRole() throws Exception {
		mockMvc
			.perform(get("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("user", "userpass")))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetEndpointWithAdminRole() throws Exception {
		mockMvc
			.perform(get("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("admin", "adminpass")))
			.andExpect(status().isOk());
	}

	@Test
	public void testPostEndpointWithUserRole() throws Exception {
		mockMvc
			.perform(post("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("user", "userpass")))
			.andExpect(status().isForbidden());
	}

	@Test
	public void testPostEndpointWithAdminRole() throws Exception {
		mockMvc
			.perform(
					post("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("admin", "adminpass")))
			.andExpect(status().isOk());
	}

	@Test
	public void testPutEndpointWithUserRole() throws Exception {
		mockMvc
			.perform(put("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("user", "userpass")))
			.andExpect(status().isForbidden());
	}

	@Test
	public void testPutEndpointWithAdminRole() throws Exception {
		mockMvc
			.perform(put("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("admin", "adminpass")))
			.andExpect(status().isOk());
	}

	@Test
	public void testDeleteEndpointWithUserRole() throws Exception {
		mockMvc
			.perform(
					delete("/api/example").header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader("user", "userpass")))
			.andExpect(status().isForbidden());
	}

	@Test
	public void testDeleteEndpointWithAdminRole() throws Exception {
		mockMvc
			.perform(delete("/api/example").header(HttpHeaders.AUTHORIZATION,
					createBasicAuthHeader("admin", "adminpass")))
			.andExpect(status().isOk());
	}

	@Test
	public void testUnauthorizedAccess() throws Exception {
		mockMvc.perform(get("/api/example")).andExpect(status().isUnauthorized());
	}

	@Test
	public void testInvalidCredentials() throws Exception {
		mockMvc
			.perform(get("/api/example").header(HttpHeaders.AUTHORIZATION,
					createBasicAuthHeader("invalid", "credentials")))
			.andExpect(status().isUnauthorized());
	}

	@TestConfiguration
	static class TestConfig {

		@Autowired
		public void testUserDetailsService(InMemoryUserDetailsManager manager, PasswordEncoder passwordEncoder) {
			UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("adminpass"))
				.roles("ADMIN", "USER")
				.build();

			UserDetails user = User.builder()
				.username("user")
				.password(passwordEncoder.encode("userpass"))
				.roles("USER")
				.build();

			if (manager.userExists(admin.getUsername())) {
				manager.deleteUser(admin.getUsername());
			}
			manager.createUser(admin);
			if (manager.userExists(user.getUsername())) {
				manager.deleteUser(user.getUsername());
			}
			manager.createUser(user);
		}

	}

	@RestController
	static class TestController {

		@RequestMapping(path = "/api/example",
				method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
		public String exampleEndpoint() {
			return "Example endpoint";
		}

	}

}
