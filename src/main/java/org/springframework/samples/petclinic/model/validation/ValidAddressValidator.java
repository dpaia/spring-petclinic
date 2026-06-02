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
package org.springframework.samples.petclinic.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator for {@link ValidAddress} constraint. Validates that an address contains at
 * least a street number and street name.
 */
public class ValidAddressValidator implements ConstraintValidator<ValidAddress, String> {

	// Pattern to match an address with at least a number followed by some text
	// This ensures the address has at least a street number and street name
	private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d+\\s+\\w+.*$");

	@Override
	public void initialize(ValidAddress constraintAnnotation) {
		// No initialization needed
	}

	@Override
	public boolean isValid(String address, ConstraintValidatorContext context) {
		// Null values are handled by @NotBlank
		if (address == null) {
			return true;
		}

		// Check if the address matches our pattern
		return ADDRESS_PATTERN.matcher(address).matches();
	}

}
