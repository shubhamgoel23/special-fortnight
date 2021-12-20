package com.demo.micro.book;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository microRepository;

	public void register(BookDto microRequest) {
		Book micro = Book.builder().firstName(microRequest.getFirstName()).build();
		microRepository.save(micro);

	}

}
