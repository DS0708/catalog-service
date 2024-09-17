package com.polarbookshop.catalogservice.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class BookValidationTests {
    private static Validator validator;

    //클래스 내의 테스트를 실행하기 전에 가장 먼저 실행할 코드 블록
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceds(){
        var book = new Book("1234567890","Title","Author",9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
        //유효성 검사에서 오류가 없음을 확인
    }

    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails(){
        var book = new Book("a234567890","Title","Author",9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
        //유효성 검사 제약 조건 위반이 잘못된 ISBN에 대한 것인지 확인
    }
}
