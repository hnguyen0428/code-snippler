package com.codesnippler.Exceptions;


import com.codesnippler.Utility.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.json.JsonObject;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(ConstraintViolationException exception) {
        JsonObject error = ResponseBuilder.createErrorObject(exception.getLocalizedMessage(), exception.getClass().getSimpleName());
        return ResponseBuilder.createErrorResponse(error).toString();
    }
}
