/*
 * Copyright 2012-2019 the original author or authors.
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuration class for enabling JPA auditing in the application. This enables
 * automatic tracking of entity creation and modification timestamps and users.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

	/**
	 * Provides the current auditor (user) for JPA auditing. In a real application, this
	 * would typically retrieve the username from Spring Security's SecurityContextHolder.
	 * For simplicity in this demo application, we return a fixed value.
	 * @return An AuditorAware implementation that provides the current user
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		// In a real application with Spring Security, you would use:
		// return () -> Optional.ofNullable(SecurityContextHolder.getContext())
		// .map(SecurityContext::getAuthentication)
		// .filter(Authentication::isAuthenticated)
		// .map(Authentication::getName);

		// For simplicity in this demo application, we return a fixed value
		return () -> Optional.of("system");
	}

}
