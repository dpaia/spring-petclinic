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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers. Provides custom error responses for
 * validation errors.
 *
 * @author Junie
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handle validation exceptions thrown by @Valid annotation
	 * @param ex the exception
	 * @return a ResponseEntity with a custom validation error response
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();

		ValidationErrorResponse errorResponse = new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(),
				"Validation Error", "The request contains invalid data");

		// Extract field errors from BindingResult
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

}
