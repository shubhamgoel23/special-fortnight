package com.demo.micro.config.security;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.StringUtils;

import com.demo.micro.config.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
			Pattern.CASE_INSENSITIVE);

	private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		// @formatter:off
		security
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests((authorizeRequests) -> authorizeRequests
				.antMatchers("/api/**").fullyAuthenticated()
				.anyRequest().permitAll())
		.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
						.accessDeniedHandler(accessDeniedHandler())
						.authenticationEntryPoint(authenticationEntryPoint())
						.authenticationManagerResolver(resolve())
						.bearerTokenResolver(getTokenResolver()));
		// @formatter:on

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/public/**");
	}

	@Bean
	AuthenticationManagerResolver<HttpServletRequest> resolve() {
		return request -> {
//			if (request.getPathInfo().startsWith("/employee")) {
//				return jwt();
//			}
			return jwtAuthenticationManager();
		};
	}

	AuthenticationManager jwtAuthenticationManager() {
//			jwtDecoder = NimbusJwtDecoder
//					.withSecretKey(Keys.hmacShaKeyFor(
//							"secursecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuree".getBytes()))
//					.build();
		JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(customJwtDecoder());
		authenticationProvider.setJwtAuthenticationConverter(customTokenConverter());
		return authenticationProvider::authenticate;
	}

	private Converter<Jwt, AbstractAuthenticationToken> customTokenConverter() {
		return jwt -> {
			OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, jwt.getTokenValue(),
					jwt.getIssuedAt(), jwt.getExpiresAt());
			Set<GrantedAuthority> simpleGrantedAuthority = fetchAuthorities(jwt);
			Map<String, Object> attributes = jwt.getClaims();
			OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(attributes,
					simpleGrantedAuthority);
			return new BearerTokenAuthentication(principal, token, simpleGrantedAuthority);
		};

	}

	private Set<GrantedAuthority> fetchAuthorities(Jwt jwt) {
		List<String> roles = jwt.getClaimAsStringList("role");
		return roles.stream().map(Role::valueOf).map(Role::getGrantedAuthorities).flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private JwtDecoder customJwtDecoder() {
		return token -> {
			try {
				JwtParser parser = Jwts.parserBuilder()
						.setSigningKey(Keys.hmacShaKeyFor(
								"secursecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuree".getBytes()))
						.build();
				return createJwt(token, parser.parseClaimsJws(token));
			} catch (Exception ex) {
				throw new BadJwtException(ex.getMessage());
			}
		};
	}

	@SuppressWarnings("unchecked")
	private Jwt createJwt(String token, Jws<Claims> jwsClaims) {

		jwsClaims.getBody().put(JwtClaimNames.IAT, fromDatetoInstant(jwsClaims.getBody().get(JwtClaimNames.IAT)));
		jwsClaims.getBody().put(JwtClaimNames.EXP, fromDatetoInstant(jwsClaims.getBody().get(JwtClaimNames.EXP)));
//		 @formatter:off
		return Jwt.withTokenValue(token)
				.headers((h) -> h.putAll(jwsClaims.getHeader()))
				.claims((c) -> c.putAll(jwsClaims.getBody()))
				.build();
		// @formatter:on
	}

	private Instant fromDatetoInstant(Object timestamp) {
		return Instant.ofEpochSecond((Integer) timestamp);
	}

//	private RSAPublicKey convertkey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		byte[] decoded = Base64.getDecoder().decode(key.getBytes());
//		EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
//		return (RSAPublicKey) kf.generatePublic(spec);
//	}

	private BearerTokenResolver getTokenResolver() {
		return request -> {
			final String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
//			return new HeaderBearerTokenResolver("X-JWT-ASSERTION");
			return authorizationHeaderToken;
		};

	}

	private String resolveFromAuthorizationHeader(HttpServletRequest request) {
		String authorization = request.getHeader(this.bearerTokenHeaderName);
		if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
			return null;
		}
		Matcher matcher = authorizationPattern.matcher(authorization);
		if (!matcher.matches()) {
			BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
			throw new OAuth2AuthenticationException(error);
		}
		return matcher.group("token");
	}

	private AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> {

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

			Throwable root = ExceptionUtils.getRootCause(accessDeniedException);
			// @formatter:off
			Response<Void> body = Response.<Void>builder()
					.status(HttpStatus.FORBIDDEN)
					.statusCode(HttpStatus.FORBIDDEN.value())
					.reason(root.getClass().getSimpleName())
					.developerMessage(root.getMessage())
					.build();
			// @formatter:on

			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);

		};
	}

	private AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			Throwable root = ExceptionUtils.getRootCause(authException);
			// @formatter:off
			Response<Void> body = Response.<Void>builder()
					.status(HttpStatus.UNAUTHORIZED)
					.statusCode(HttpStatus.UNAUTHORIZED.value())
					.reason(root.getClass().getSimpleName())
					.developerMessage(root.getMessage())
					.build();
			// @formatter:on
			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);

		};
	}
}