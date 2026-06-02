/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.test.context.aot.DisabledInAotMode;

/**
 * Test class for {@link EntityReflectionUtil}. These tests will fail in GraalVM native
 * image without proper reflection configuration.
 */
@SpringBootTest
public class EntityReflectionUtilTests {

	@Autowired
	private EntityReflectionUtil reflectionUtil;

	/**
	 * Test loading a class by name. This will fail in native image without proper
	 * reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testLoadClassByName() {
		assertDoesNotThrow(() -> {
			Class<?> baseEntityClass = reflectionUtil.loadClassByName("BaseEntity");
			assertNotNull(baseEntityClass);
			assertEquals("BaseEntity", baseEntityClass.getSimpleName());

			Class<?> personClass = reflectionUtil.loadClassByName("Person");
			assertNotNull(personClass);
			assertEquals("Person", personClass.getSimpleName());
		});
	}

	/**
	 * Test creating an instance using reflection. This will fail in native image without
	 * proper reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testCreateInstance() {
		assertDoesNotThrow(() -> {
			Object person = reflectionUtil.createInstance("Person");
			assertNotNull(person);
			assertTrue(person instanceof Person);
		});
	}

	/**
	 * Test getting method names using reflection. This will fail in native image without
	 * proper reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testGetMethodNames() {
		assertDoesNotThrow(() -> {
			String[] methodNames = reflectionUtil.getMethodNames("Person");
			assertNotNull(methodNames);
			assertThat(methodNames).contains("getFirstName", "setFirstName", "getLastName", "setLastName");
		});
	}

	/**
	 * Test getting field names using reflection. This will fail in native image without
	 * proper reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testGetFieldNames() {
		assertDoesNotThrow(() -> {
			String[] fieldNames = reflectionUtil.getFieldNames("Person");
			assertNotNull(fieldNames);
			assertThat(fieldNames).contains("firstName", "lastName");
		});
	}

	/**
	 * Test setting and getting property values using reflection. This will fail in native
	 * image without proper reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testPropertyAccess() {
		assertDoesNotThrow(() -> {
			Object person = reflectionUtil.createInstance("Person");

			reflectionUtil.setProperty(person, "firstName", "John");
			reflectionUtil.setProperty(person, "lastName", "Doe");

			assertEquals("John", reflectionUtil.getProperty(person, "firstName"));
			assertEquals("Doe", reflectionUtil.getProperty(person, "lastName"));
		});
	}

	/**
	 * Test inspecting a class using reflection. This will fail in native image without
	 * proper reflection configuration.
	 */
	@Test
	@DisabledInNativeImage
	@DisabledInAotMode
	public void testInspectClass() {
		assertDoesNotThrow(() -> {
			Map<String, Object> info = reflectionUtil.inspectClass("Person");

			assertNotNull(info);
			assertEquals("Person", info.get("className"));
			assertEquals("org.springframework.samples.petclinic.model.Person", info.get("fullClassName"));
			assertEquals(true, info.get("isEntity"));
			assertEquals("BaseEntity", info.get("superclass"));

			String[] methods = (String[]) info.get("methods");
			assertThat(methods).contains("getFirstName", "setFirstName", "getLastName", "setLastName");

			String[] fields = (String[]) info.get("fields");
			assertThat(fields).contains("firstName", "lastName");
		});
	}

}
