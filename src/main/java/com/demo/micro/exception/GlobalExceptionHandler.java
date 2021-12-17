package com.demo.micro.exception;

import static com.demo.micro.util.HelperClass.collectionAsStream;
import static com.demo.micro.util.HelperClass.requestMethod;
import static com.demo.micro.util.HelperClass.requestUrl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.demo.micro.dto.Response;
import com.demo.micro.dto.ValidationError;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ BusinessException.class })
//	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	protected ResponseEntity<Object> handleBusinessException(final BusinessException ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Object body = Response.<Object>builder().status(ex.getHttpStatus()).statusCode(ex.getHttpStatus().value())
				.reason(ex.getCode()).developerMessage(ex.getLocalizedMessage()).build();
		return handleExceptionInternal(ex, body, headers, ex.getHttpStatus(), request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		log.error("Request {} {} failed with exception reason: {}", requestMethod.get().orElse("null"),
				requestUrl.get().orElse("null"), ex.getMessage(), ex);
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
		}
		Throwable root = ExceptionUtils.getRootCause(ex);

		headers.setContentType(MediaType.APPLICATION_JSON);
		body = null == body
				? Response.<Object>builder().status(status).statusCode(status.value())
						.reason(root.getClass().getSimpleName()).developerMessage(root.getMessage()).build()
				: body;

		return new ResponseEntity<>(body, headers, status);
	}

	/**
	 * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
	 * validation.
	 *
	 * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
	 *                validation fails
	 * @param headers HttpHeaders
	 * @param status  HttpStatus
	 * @param request WebRequest
	 * @return the ApiError object
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<ValidationError> errors = addValidationErrors(ex.getBindingResult().getFieldErrors());
		errors.addAll(addValidationErrors(ex.getBindingResult().getGlobalErrors()));

		Object body = Response.<Object>builder().status(status).statusCode(status.value())
				.reason(ex.getClass().getSimpleName()).developerMessage("Validation error")
				.data(Collections.singletonMap("errors", errors)).build();

		return handleExceptionInternal(ex, body, headers, status, request);
	}

	/**
	 * Handles javax.validation.ConstraintViolationException. Thrown when @Validated
	 * fails.
	 *
	 * @param ex the ConstraintViolationException
	 * @return the ApiError object
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		List<ValidationError> errors = addValidationErrors(ex.getConstraintViolations());
		Object body = Response.<Object>builder().status(status).statusCode(status.value())
				.reason(ex.getClass().getSimpleName()).developerMessage("Validation error")
				.data(Collections.singletonMap("errors", errors)).build();

		return handleExceptionInternal(ex, body, headers, status, request);
	}

	public List<ValidationError> addValidationErrors(List<? extends ObjectError> fieldErrors) {
		return collectionAsStream(fieldErrors).map(this::addValidationError).collect(Collectors.toList());

	}

	private ValidationError addValidationError(String object, String message) {
		return ValidationError.builder().object(object).message(message).build();
	}

	private ValidationError addValidationError(String object, String field, Object rejectedValue, String message) {
		return ValidationError.builder().object(object).field(field).rejectedValue(rejectedValue).message(message)
				.build();
	}

	private ValidationError addValidationError(ObjectError objectError) {
		if (objectError instanceof FieldError) {
			FieldError fieldError = (FieldError) objectError;
			return this.addValidationError(fieldError.getObjectName(), fieldError.getField(),
					fieldError.getRejectedValue(), fieldError.getDefaultMessage());
		} else {
			return this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
		}

	}

	/**
	 * Utility method for adding error of ConstraintViolation. Usually when
	 * a @Validated validation fails.
	 *
	 * @param cv the ConstraintViolation
	 */
	private ValidationError addValidationError(ConstraintViolation<?> cv) {
		return this.addValidationError(cv.getRootBeanClass().getSimpleName(),
				((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), cv.getInvalidValue(), cv.getMessage());
	}

	public List<ValidationError> addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
		return collectionAsStream(constraintViolations).map(this::addValidationError).collect(Collectors.toList());
	}

}
