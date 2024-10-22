package com.web.app.controller;

import com.web.app.exception.UserNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class is responsible for handling exceptions globally across the whole application.
 * It catches exceptions and presents a user-friendly error page.
 */
@ControllerAdvice
public class ExceptionHandlerController {

    protected static final String ERROR_PAGE = "error-page";
    protected static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute(ERROR_MESSAGE_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }
}
