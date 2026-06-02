package org.springframework.samples.petclinic.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for API key authentication components using X-API-KEY header.
 */
@SpringBootTest
@Import({ ApiKeyAuthenticationTest.TestController.class })
public class ApiKeyAuthenticationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private static final String VALID_API_KEY = "test-api-key";

	private static final String INVALID_API_KEY = "invalid-api-key";

	private static final String API_KEY_HEADER = "X-API-KEY";

	@BeforeEach
	void setUp() {
		// Clear security context before each test
		SecurityContextHolder.clearContext();

		// Set up MockMvc with security
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	void testApiKeyAuthentication_ValidKey() throws Exception {
		// Create a request with a valid API key and verify it's allowed (200 OK)
		mockMvc.perform(get("/partners/test").header(API_KEY_HEADER, VALID_API_KEY)).andExpect(status().isOk());

		// The 200 OK status confirms that authentication was successful,
		// as the endpoint is protected and only accessible with a valid API key
	}

	@Test
	void testApiKeyAuthentication_InvalidKey() throws Exception {
		// Create a request with an invalid API key
		MvcResult result = mockMvc.perform(get("/partners/test").header(API_KEY_HEADER, INVALID_API_KEY))
			.andExpect(status().isUnauthorized())
			.andReturn();

		// Verify that the response has an unauthorized status
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, result.getResponse().getStatus());

		// Verify that no authentication is in the security context
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void testApiKeyAuthentication_NullKey() throws Exception {
		// Create a request without an API key
		MvcResult result = mockMvc.perform(get("/partners/test")).andExpect(status().isUnauthorized()).andReturn();

		// Verify that the response has an unauthorized status
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, result.getResponse().getStatus());

		// Verify that no authentication is in the security context
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	/**
	 * Test controller that handles the /partners/test endpoint.
	 */
	@RestController
	static class TestController {

		@GetMapping("/partners/test")
		public String partnerTest(Principal principal, HttpServletResponse httpServletResponse,
				@RequestHeader(API_KEY_HEADER) String apiKey) throws IOException {
			if (principal == null || apiKey == null || apiKey.isEmpty()) {
				httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key is missing or empty");
				return "Not authenticated";
			}
			return principal.getName();
		}

	}

}
