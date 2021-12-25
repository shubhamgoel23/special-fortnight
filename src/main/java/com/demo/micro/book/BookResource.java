package com.demo.micro.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/micro")
@RequiredArgsConstructor
public class BookResource {

    private final BookService microService;

    @PostMapping
    public void registerCustomer(@RequestBody BookDto microRequest) {
        log.info("new request {}", microRequest);
        microService.register(microRequest);
    }

}
