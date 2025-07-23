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
package org.springframework.samples.petclinic;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test specifically designed to verify Flyway migrations work correctly in a
 * GraalVM native image. Tests that the database schema is properly created and populated
 * with data using Flyway migrations.
 */
@SpringBootTest
public class FlywayMigrationIntegrationTest {

	@Autowired
	private Flyway flyway;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private VetRepository vetRepository;

	/**
	 * Test that Flyway is properly configured and migrations have been applied.
	 */
	@Test
	void testFlywayMigrations() {
		// Verify that Flyway is properly configured
		assertThat(flyway).isNotNull();

		// Verify that migrations have been applied
		assertThat(flyway.info().current().getVersion().toString()).isEqualTo("2");
		assertThat(flyway.info().current().getDescription()).isEqualTo("load data");
	}

	/**
	 * Test that the database schema has been properly created by Flyway.
	 */
	@Test
	void testDatabaseSchema() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// Verify that all tables have been created
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "vets")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "specialties")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "vet_specialties")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "types")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "owners")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "pets")).isGreaterThan(0);
		assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "visits")).isGreaterThan(0);
	}

	/**
	 * Test that the data has been properly loaded by Flyway.
	 */
	@Test
	void testDataLoaded() {
		// Verify that owners have been loaded
		assertThat(ownerRepository.findAll()).hasSize(10);

		// Verify that vets have been loaded
		assertThat(vetRepository.findAll()).hasSize(6);
	}

}
