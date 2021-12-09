package com.demo.micro.util;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.base.CharMatcher;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HelperClass {

	public static Supplier<Long> currentTimeMillis = () -> System.currentTimeMillis();

	public static Supplier<Optional<HttpServletRequest>> httpServletRequest = () -> {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.filter(ServletRequestAttributes.class::isInstance).map(ServletRequestAttributes.class::cast)
				.map(ServletRequestAttributes::getRequest);
	};

	public static Supplier<Optional<String>> requestUrl = () -> {
		return httpServletRequest.get().map(HttpServletRequest::getRequestURI);
	};

	public static Supplier<Optional<String>> requestMethod = () -> {
		return httpServletRequest.get().map(HttpServletRequest::getMethod);
	};

	public static Supplier<Optional<String>> currentTraceId = () -> Optional.ofNullable(MDC.get("traceId"));

	public static Function<String, String> sanitizeString = source -> {
		PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
		return policy.sanitize(source);
	};

	public static Function<String, String> trimString = source -> {
		return source != null ? source.trim() : null;
	};

	public static Predicate<String> isStringWithoutSpecialCharacters = source -> {
		return CharMatcher
//				.anyOf("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")
				.anyOf("'\"`;*%_=&\\\\|*?~<>^()[]{}$\\n\\r").matchesNoneOf(source);
	};

	public static <T> Stream<T> collectionAsStream(Collection<T> collection) {
		return collection == null ? Stream.empty() : collection.stream();
	}
}
