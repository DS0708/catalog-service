package com.polarbookshop.catalogservice.demo;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("testdata") //이 클래스를 testdata 프로파일에 할당, 즉 testdata 프로파일이 활성화될 때만 로드된다.
public class BookDataLoader {
    private final BookRepository bookRepository;
    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    //ApplicationReadyEvent가 발생하면 테스트 데이터 생성이 시작
    //이 이벤트는 애플리케이션 시작 단계가 완료되면 발생
    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
        bookRepository.deleteAll();
        // Spring Data JDBC 프레임워크가 내부적으로 식별자와 버전에 대한 할당 값을 처리
        var book1 = Book.of("1234567891", "Northern Lights", "Lyra Silverstar", 9.90, "Polarsophia1");
        var book2 = Book.of("1234567892", "Polar Journey", "Iorek Polarson", 12.90, "Polarsophia2");
        bookRepository.saveAll(List.of(book1, book2));
    }
}
