package com.demo.micro.config;

import static com.demo.micro.util.HelperClass.currentTimeMillis;
import static com.demo.micro.util.HelperClass.currentTraceId;
import static com.demo.micro.util.HelperClass.requestMethod;
import static com.demo.micro.util.HelperClass.requestUrl;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonInclude(value = Include.NON_NULL)
public class Response<T> {

	@Builder.Default
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected long timeStamp = currentTimeMillis.get();

	@Builder.Default
	protected Optional<String> traceId = currentTraceId.get();

	@Builder.Default
	protected Optional<String> method = requestMethod.get();

	@Builder.Default
	protected Optional<String> path = requestUrl.get();

	protected HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected int statusCode;
	protected String reason;
	protected String message;
	protected String developerMessage;
	protected Map<String, T> data;
}
