package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

/*
REST API 오류를 처리하기 위해 자바 표준 예외를 사용하고
@RestControllerAdvice 클래스를 통해 주어진 예외가 발생할 때 어떤 작업을 수행할지 정의하면 된다.
예외를 처리하는 코드와 예외를 발생하는 코드를 분리하는 중앙식 접근법
 */
@RestControllerAdvice //클래스가 중앙식 예외 핸들러임을 표시
public class BookControllerAdvice {

    @ExceptionHandler(BookNotFoundException.class) //실행되어야할 대상인 Exception 정의
    @ResponseStatus(HttpStatus.NOT_FOUND) //예외 발생시, HTTP 응답에 포함할 상태 코드 정의, 404
    String bookNotFoundHandler(BookNotFoundException ex) {
        //HTTP 응답 본문에 포함할 메시지
        return ex.getMessage();
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) //처리할 수 없는 개체 : 상태코드 422
    String bookAlreadyExistsHandler(BookAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //Book 객체에서 하나 이상의 필드가 잘못되었을 때는 400 (잘못된 요청)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        //책 데이터 유효성 검증이 실패한 경우 발생하는 예외 처리 logic
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            //빈 메시지 대신 의미 있는 오류 메시지를 위해 유효하지 않은 필드 확인
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
