package com.demo.micro.book;

import org.springframework.stereotype.Service;

@Service
public record BookService(BookRepository microRepository) {

	public void register(BookDto microRequest) {
		Book micro = Book.builder().firstName(microRequest.firstName()).build();
		microRepository.save(micro);

	}

}
