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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JavaBean object that represents a validation error response. Contains metadata
 * about the error and a list of field-level validation errors.
 *
 * @author Junie
 */
public class ValidationErrorResponse {

	private LocalDateTime timestamp;

	private int status;

	private String error;

	private String message;

	private List<ValidationError> fieldErrors = new ArrayList<>();

	/**
	 * Default constructor
	 */
	public ValidationErrorResponse() {
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * Constructor with status, error, and message
	 * @param status the HTTP status code
	 * @param error the error type
	 * @param message the error message
	 */
	public ValidationErrorResponse(int status, String error, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.error = error;
		this.message = message;
	}

	/**
	 * Add a field error to the response
	 * @param field the field name
	 * @param message the error message
	 */
	public void addFieldError(String field, String message) {
		this.fieldErrors.add(new ValidationError(field, message));
	}

	/**
	 * Get the timestamp
	 * @return the timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * Set the timestamp
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Get the HTTP status code
	 * @return the status code
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set the HTTP status code
	 * @param status the status code
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get the error type
	 * @return the error type
	 */
	public String getError() {
		return error;
	}

	/**
	 * Set the error type
	 * @param error the error type
	 */
	public void setError(String error) {
		this.error = error;
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

	/**
	 * Get the list of field errors
	 * @return the list of field errors
	 */
	public List<ValidationError> getFieldErrors() {
		return fieldErrors;
	}

	/**
	 * Set the list of field errors
	 * @param fieldErrors the list of field errors
	 */
	public void setFieldErrors(List<ValidationError> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

}
