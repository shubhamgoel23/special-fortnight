package com.demo.micro.config.security;

public enum Permission {

	STUDENT_READ("student:read"), STUDENT_WRITE("student:write"), COURSE_READ("course:read"),
	COURSE_WRITE("course:write");

	private final String permission;

	Permission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

}
