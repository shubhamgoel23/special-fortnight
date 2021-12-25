package com.demo.micro.authentication;

import com.demo.micro.config.security.HasRole;
import com.demo.micro.config.security.Role;
import com.demo.micro.exception.BusinessException;
import com.demo.micro.exception.BusinessExceptionReason;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

import static com.demo.micro.config.security.Role.ADMIN;

@RestController
public class TokenController {

    @GetMapping(value = "/test")
//	@PreAuthorize("hasAuthority('student:write')")
    public ResponseEntity<String> getToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = Jwts.builder().setSubject("shubham").claim("role", Role.STUDENT).setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1l)))
                .signWith(
                        Keys.hmacShaKeyFor(
                                "secursecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuree".getBytes()),
                        SignatureAlgorithm.HS512)
                .compact();
        throw new BusinessException(BusinessExceptionReason.TODO_NOT_FOUND_BY_EXT_REF);
//		return ResponseEntity.ok(token);
    }

    @GetMapping(value = "/test1")
//	@PreAuthorize("hasAuthority('student:write')")
//	@HasPermission(permission = {Permission.COURSE_READ})
//	@HasRole({ ADMIN, STUDENT })
    @HasRole({ADMIN})
    public ResponseEntity<String> getToken1() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = Jwts.builder().setSubject("shubham").claim("role", Role.ADMIN).setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now()))
                .signWith(
                        Keys.hmacShaKeyFor(
                                "secursecuresecuresecuresecuresecuresecuresecuresecuresecuresecuresecuree".getBytes()),
                        SignatureAlgorithm.HS512)
                .compact();
        return ResponseEntity.ok(token);
    }

}
