package com.demo.micro.config;

import static com.demo.micro.util.HelperClass.currentTimeMillis;
import static com.demo.micro.util.HelperClass.currentTraceId;
import static com.demo.micro.util.HelperClass.requestMethod;
import static com.demo.micro.util.HelperClass.requestUrl;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
//@JsonInclude(value = Include.NON_NULL)
public class Response<T> {

	@Builder.Default
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected long timeStamp = currentTimeMillis.get();

	@Builder.Default
	protected String traceId = currentTraceId.get().orElse(null);

	@Builder.Default
	protected String method = requestMethod.get().orElse(null);

	@Builder.Default
	protected String path = requestUrl.get().orElse(null);

	protected HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected int statusCode;
	protected String reason;
	protected String message;
	protected String developerMessage;
	protected Map<String, T> data;
}
