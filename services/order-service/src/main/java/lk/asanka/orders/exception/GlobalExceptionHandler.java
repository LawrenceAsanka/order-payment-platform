package lk.asanka.orders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFound(ProductNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleProductServiceIsUnavailable(ProductServiceUnavailableException e){
        return e.getMessage();
    }
}
