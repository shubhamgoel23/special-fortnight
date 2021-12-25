package com.demo.micro.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class ErrorHandler implements ErrorController {

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if (request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) != null) {
            throw (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        }
    }

}
