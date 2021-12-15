package com.demo.micro.config.security;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class HasAuthoritiesAspect {

	@Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(permission)")
	public void hasPermission(final JoinPoint joinPoint, final HasPermission permission) {

		validate(Stream.of(permission.permission()).map(Permission::getPermission));
	}

	@Before("within(@org.springframework.web.bind.annotation.RestController *) &&  @annotation(role)")
	public void hasRole(final JoinPoint joinPoint, final HasRole role) {

		validate(Stream.of(role.role()).map(Role::getRole));
	}

	private void validate(Stream<String> authorities) {

		final SecurityContext securityContext = SecurityContextHolder.getContext();
		if (!Objects.isNull(securityContext)) {
			final Authentication authentication = securityContext.getAuthentication();
			if (!Objects.isNull(authentication)) {
				final String username = authentication.getName();

				final Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

				if (authorities.noneMatch(authorityName -> userAuthorities.stream()
						.anyMatch(userAuthority -> authorityName.equalsIgnoreCase(userAuthority.getAuthority())))) {

					log.error("User {} does not have the correct authorities required by endpoint", username);
//                    throw new ApiException(DefaultExceptionReason.FORBIDDEN);
					throw new AccessDeniedException("Access is denied");
				}
			} else {
				log.error("The authentication is null when checking endpoint access for user request");
//                throw new ApiException(DefaultExceptionReason.UNAUTHORIZED);
				throw new AccessDeniedException("Access is denied");
			}
		} else {
			log.error("The security context is null when checking endpoint access for user request");
//            throw new ApiException(DefaultExceptionReason.FORBIDDEN);
			throw new AccessDeniedException("Access is denied");
		}

	}

}
