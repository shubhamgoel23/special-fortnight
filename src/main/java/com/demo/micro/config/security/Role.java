package com.demo.micro.config.security;

import static com.demo.micro.config.security.Permission.COURSE_READ;
import static com.demo.micro.config.security.Permission.COURSE_WRITE;
import static com.demo.micro.config.security.Permission.STUDENT_READ;
import static com.demo.micro.config.security.Permission.STUDENT_WRITE;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;

public enum Role {

	STUDENT(Sets.newHashSet(COURSE_READ, COURSE_WRITE)),
	ADMIN(Sets.newHashSet(STUDENT_WRITE, COURSE_READ, COURSE_WRITE, STUDENT_READ));

	private final Set<Permission> permissions;

	Role(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getRole() {
		return "ROLE_" + this.name();
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public Set<GrantedAuthority> getGrantedAuthorities() {
		Set<GrantedAuthority> permissions = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return permissions;
	}

}
