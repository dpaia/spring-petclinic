package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class MigrationTest {

	@Test
	public void testSpringBootVersion() {
		String springBootVersion = SpringBootVersion.getVersion();
		assertTrue(springBootVersion.startsWith("3.4."),
				"Expected Spring Boot version 3.4.x, but found: " + springBootVersion);
	}

	@Test
	public void testLibraries() {
		String springDocVersion = getVersionFromClasspath("org.springdoc", "springdoc-openapi-starter-webmvc-ui");
		assertNotNull(springDocVersion, "Could not find springdoc-openapi-tests version");

		assertTrue(springDocVersion.startsWith("2.8."),
				"Expected org.springdoc:springdoc-openapi-tests version 2.8.x, but found: " + springDocVersion);

		String webjarsCoreVersion = getVersionFromClasspath("org.webjars", "webjars-locator-core");
		assertNull(webjarsCoreVersion, "Found webjars-locator-core version");

		String webjarsLiteVersion = getVersionFromClasspath("org.webjars", "webjars-locator-lite");
		assertNotNull(webjarsLiteVersion, "Could not find webjars-locator-lite version");
		assertTrue(webjarsLiteVersion.startsWith("1.0."),
				"Expected org.webjars:webjars-locator-lite version 1.0.x, but found: " + webjarsLiteVersion);

		String flywayMySqlVersion = getVersionFromClasspath("org.flywaydb", "flyway-mysql");
		assertNotNull(flywayMySqlVersion, "Could not find flyway-mysql version");

		String flywayPostgresVersion = getVersionFromClasspath("org.flywaydb", "flyway-database-postgresql");
		assertNotNull(flywayPostgresVersion, "Could not find flyway-postgresql version");
	}

	private String getVersionFromClasspath(String groupId, String artifactId) {
		String resourcePath = "META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
			if (inputStream != null) {
				Properties props = new Properties();
				props.load(inputStream);
				return props.getProperty("version");
			}
		}
		catch (IOException e) {
			System.err
					.println("Failed to load version info for " + groupId + ":" + artifactId + " - " + e.getMessage());
		}

		// Fallback: try to get version from Package information
		try {
			Package pkg = Package.getPackage(groupId);
			if (pkg != null) {
				String version = pkg.getImplementationVersion();
				if (version != null) {
					return version;
				}
				return pkg.getSpecificationVersion();
			}
		}
		catch (Exception e) {
			System.err.println("Failed to get package version for " + groupId + " - " + e.getMessage());
		}

		return null;
	}

	@Test
	public void testProperties() throws IOException {
		Properties appProperties = loadApplicationProperties("");
		assertNull(appProperties.getProperty("management.metrics.web.client.requests-metric-name"),
				"Old property 'management.metrics.web.client.requests-metric-name' should not be present");
		assertNull(appProperties.getProperty("management.metrics.web.server.requests-metric-name"),
				"Old property 'management.metrics.web.server.requests-metric-name' should not be present");

		assertEquals("http.client.requests",
				appProperties.getProperty("management.observations.http.client.requests.name"),
				"New property 'management.observations.http.client.requests.name' should have correct value");
		assertEquals("http.server.requests",
				appProperties.getProperty("management.observations.http.server.requests.name"),
				"New property 'management.observations.http.server.requests.name' should have correct value");

		assertNull(appProperties.getProperty("spring.flyway.license-key"),
				"Property 'spring.flyway.license-key' should not be active");

		Properties couchbaseProperties = loadApplicationProperties("-couchbase");
		assertNull(couchbaseProperties.getProperty("spring.couchbase.env.ssl.key-store"),
				"Property 'spring.couchbase.env.ssl.key-store' should not be active");
		assertNull(couchbaseProperties.getProperty("spring.couchbase.env.ssl.key-store-password"),
				"Property 'spring.couchbase.env.ssl.key-store-password' should not be active");
	}

	private Properties loadApplicationProperties(String suffix) throws IOException {
		Properties props = new Properties();
		try (InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("application" + suffix + ".properties")) {
			assertNotNull(inputStream, "application.properties file should exist");
			props.load(inputStream);
		}
		return props;
	}

	@Test
	public void testBeans() throws ClassNotFoundException {
		// Test that constructors in these classes don't have @Autowired annotation
		assertConstructorNotAutowired("org.springframework.samples.petclinic.owner.OwnerController");
		assertConstructorNotAutowired("org.springframework.samples.petclinic.owner.PetTypeFormatter");
		assertConstructorNotAutowired("org.springframework.samples.petclinic.owner.VisitController");
	}

	private void assertConstructorNotAutowired(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		java.lang.reflect.Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		for (java.lang.reflect.Constructor<?> constructor : constructors) {
			// Skip synthetic constructors (e.g., generated by inner classes)
			if (constructor.isSynthetic()) {
				continue;
			}

			// Check if the constructor has @Autowired annotation
			boolean hasAutowired = constructor
					.isAnnotationPresent(org.springframework.beans.factory.annotation.Autowired.class);

			assertFalse(hasAutowired, String.format("Constructor in %s should not have @Autowired annotation. "
					+ "Spring 4.3+ automatically injects dependencies through single constructor without @Autowired.",
					clazz.getSimpleName()));
		}
	}

	@Test
	public void testImports() throws IOException {
		// Test that PetValidator imports java.util.Base64 and not
		// org.springframework.util.Base64Utils
		String petValidatorSource = loadJavaSourceFile("org.springframework.samples.petclinic.owner.PetValidator");

		// Check that it imports java.util.Base64
		assertTrue(petValidatorSource.contains("import java.util.Base64;"),
				"PetValidator should import java.util.Base64");

		// Check that it does NOT import org.springframework.util.Base64Utils (deprecated)
		assertFalse(petValidatorSource.contains("import org.springframework.util.Base64Utils;"),
				"PetValidator should not import deprecated org.springframework.util.Base64Utils");

		// Verify the source uses Base64.getEncoder() and Base64.getDecoder() (modern API)
		assertTrue(petValidatorSource.contains("Base64.getEncoder()"),
				"PetValidator should use Base64.getEncoder() (modern API)");
		assertTrue(petValidatorSource.contains("Base64.getDecoder()"),
				"PetValidator should use Base64.getDecoder() (modern API)");

		// Verify it does NOT use the old Base64Utils methods
		assertFalse(petValidatorSource.contains("Base64Utils.encodeToString"),
				"PetValidator should not use deprecated Base64Utils.encodeToString");
		assertFalse(petValidatorSource.contains("Base64Utils.decodeFromString"),
				"PetValidator should not use deprecated Base64Utils.decodeFromString");
	}

	private String loadJavaSourceFile(String className) throws IOException {
		String filePath = "src/main/java/" + className.replace('.', '/') + ".java";
		try (InputStream inputStream = new java.io.FileInputStream(filePath)) {
			return new String(inputStream.readAllBytes());
		}
	}

	@Test
	public void testRestTemplate() throws IOException {
		String restTemplateConfigSource = loadJavaSourceFile(
				"org.springframework.samples.petclinic.config.RestTemplateConfig");

		assertFalse(restTemplateConfigSource.contains(".setConnectTimeout("));
		assertFalse(restTemplateConfigSource.contains(".setReadTimeout("));
		assertTrue(restTemplateConfigSource.contains(".connectTimeout("));
		assertTrue(restTemplateConfigSource.contains(".readTimeout("));
	}

}
