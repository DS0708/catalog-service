package com.polarbookshop.catalogservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

//unit test

/*Mockito를 사용하여 의존성 주입을 자동으로 처리하도록 설정
MockitoExtension은 JUnit 5 확장 기능으로, 테스트 실행 시
Mockito 초기화와 모의 객체 생성을 자동으로 처리*/
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    // 모의 객체 생성
    @Mock
    private BookRepository bookRepository;
    // 테스트할 BookService 객체에 자동으로 모의 객체 BookRepository를 주입
    @InjectMocks
    private BookService bookService;

    @Test
    void whenBookToCreateAlreadyExistsThenThrows(){
        var bookIsbn = "1234561232";
        var bookToCreate = new Book(bookIsbn, "Title", "Author", 9.90);
        //특정 ISBN의 책이 이미 존재한다고 가정
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        //예외가 발생하고 해당 예외의 타입과 메시지가 기대하는 바와 일치하는지 검사
        assertThatThrownBy(() -> bookService.addBookToCatalog(bookToCreate))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("A book with ISBN " + bookIsbn + " already exists.");
    }

    @Test
    void whenBookToReadDoesNotExistThenThrows() {
        var bookIsbn = "1234561232";
        when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.viewBookDetails(bookIsbn))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("The book with ISBN " + bookIsbn + " was not found.");
    }
}
