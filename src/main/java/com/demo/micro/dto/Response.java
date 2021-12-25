package com.demo.micro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.demo.micro.util.HelperClass.*;

@Data
@SuperBuilder
//@JsonInclude(value = Include.NON_NULL)
public class Response<T, R> {

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected long timeStamp = currentTimeMillis.getAsLong();

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
    protected Map<String, R> pagination;
}
