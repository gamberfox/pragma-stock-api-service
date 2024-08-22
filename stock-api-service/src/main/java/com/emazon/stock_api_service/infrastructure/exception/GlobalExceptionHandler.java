package com.emazon.stock_api_service.infrastructure.exception;

import com.emazon.stock_api_service.domain.exception.category.CategoryUseCaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CategoryUseCaseException.class)
    public ResponseEntity<String> handleCategoryUseCaseException(CategoryUseCaseException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getErrorType().getDescription()+": " + ex.getMessage());
    }
    @ExceptionHandler(CategoryPersistenceException.class)
    public ResponseEntity<String> handleCategoryPersistenceException(CategoryPersistenceException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


}
