package com.demo.micro.config;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

	@Override
	public String provide() {
		return "System";
	}
}