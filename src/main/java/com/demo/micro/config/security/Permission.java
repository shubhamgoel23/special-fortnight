package com.demo.micro.config.security;

public enum Permission {

    STUDENT_READ("student:read"), STUDENT_WRITE("student:write"), COURSE_READ("course:read"),
    COURSE_WRITE("course:write");

    private final String permissions;

    Permission(String permissions) {
        this.permissions = permissions;
    }

    public String getPermission() {
        return permissions;
    }

}
