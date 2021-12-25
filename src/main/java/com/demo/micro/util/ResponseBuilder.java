package com.demo.micro.util;

import com.demo.micro.dto.Response;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@UtilityClass
public class ResponseBuilder {

    public static <T, R> ResponseEntity<Response<T, R>> build(T data, R pagination, HttpStatus status, String message) {
        return ResponseEntity.ok(Response.<T, R>builder()
                .status(status)
                .statusCode(status.value())
                .message(message)
                .data(Map.of("data", data))
                .pagination(Map.of("pagination", pagination))
                .build());
    }

    public static <T> ResponseEntity<Response<T, Void>> build(T data, HttpStatus status, String message) {
        return ResponseEntity.ok(Response.<T, Void>builder()
                .status(status)
                .statusCode(status.value())
                .message(message)
                .data(Map.of("data", data))
                .build());
    }

    public static <T> Response<T, Void> build(T errors, HttpStatus status, String message, String reason) {

        return Response.<T, Void>builder()
                .status(status)
                .statusCode(status.value())
                .reason(reason)
                .developerMessage(message)
                .data(Map.of("errors", errors))
                .build();
    }

    public static Response<Void, Void> build(HttpStatus status, String message, String reason) {

        return Response.<Void, Void>builder()
                .status(status)
                .statusCode(status.value())
                .reason(reason)
                .developerMessage(message)
                .build();
    }

}
