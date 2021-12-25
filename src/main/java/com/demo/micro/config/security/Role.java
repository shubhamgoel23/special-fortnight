package com.demo.micro.config.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.demo.micro.config.security.Permission.*;

public enum Role {

    STUDENT(Sets.newHashSet(COURSE_READ, COURSE_WRITE)),
    ADMIN(Sets.newHashSet(STUDENT_WRITE, COURSE_READ, COURSE_WRITE, STUDENT_READ));

    private final Set<Permission> rolePermissions;

    Role(Set<Permission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public String getRole() {
        return "ROLE_" + this.name();
    }

    public Set<Permission> getPermissions() {
        return rolePermissions;
    }

    public Set<GrantedAuthority> getGrantedAuthorities() {
        Set<GrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }

}
