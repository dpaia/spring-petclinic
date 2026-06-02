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
package org.springframework.samples.petclinic.system;

/**
 * Simple JavaBean object that represents a field-level validation error. Used to provide
 * detailed validation information in API responses.
 *
 * @author Junie
 */
public class ValidationError {

	private String field;

	private String message;

	/**
	 * Default constructor
	 */
	public ValidationError() {
	}

	/**
	 * Constructor with field and message
	 * @param field the field name that has the validation error
	 * @param message the validation error message
	 */
	public ValidationError(String field, String message) {
		this.field = field;
		this.message = message;
	}

	/**
	 * Get the field name
	 * @return the field name
	 */
	public String getField() {
		return field;
	}

	/**
	 * Set the field name
	 * @param field the field name
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Get the error message
	 * @return the error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the error message
	 * @param message the error message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
