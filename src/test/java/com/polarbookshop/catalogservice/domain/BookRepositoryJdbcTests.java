package com.polarbookshop.catalogservice.domain;

import com.polarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

// Slice Test

@DataJdbcTest //Spring Data JDBC 컴포넌트를 집중적으로 테스트하는 Class
@Import(DataConfig.class) //Auditing를 활성화하기 위해 필요
@AutoConfigureTestDatabase( //TestContainer를 이용해야 하기 때문에 내장 테스트 DB 사용을 비활성화
        replace = AutoConfigureTestDatabase.Replace.NONE    //slice test할 때는 스프링 부트는 인메모리 DB를 사용하므로 비활성화 해줘야함.
)
@ActiveProfiles("integration") //application-integration.yaml에서 설정을 로드하기 위해 선언
public class BookRepositoryJdbcTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate; //DB와 상호작용하기 위한 low-level 객체

    @Test
    void findBookByIsbnWhenExisting(){
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90);
        jdbcAggregateTemplate.insert(book);
        //@DataJdbcTest annotation 덕분에 모든 메서드를 트랜잭션으로 실행하고 실행이 끝나면 롤백을 수행하기 때문에 DB에는 데이터가 남지 않는다.

        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }

    @Test
    void findAllBooks() {
        var book1 = Book.of("1234561235", "Title", "Author", 12.90);
        var book2 = Book.of("1234561236", "Another Title", "Author", 12.90);
        jdbcAggregateTemplate.insert(book1);
        jdbcAggregateTemplate.insert(book2);

        Iterable<Book> actualBooks = bookRepository.findAll();

        assertThat(StreamSupport.stream(actualBooks.spliterator(), true) //병렬 처리에 최적화 되어 있는 spliterator 생성
                .filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()))
                .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    void findBookByIsbnWhenNotExisting() {
        Optional<Book> actualBook = bookRepository.findByIsbn("1234561238");
        assertThat(actualBook).isEmpty();
    }

    @Test
    void existsByIsbnWhenExisting() {
        var bookIsbn = "1234561239";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90);
        jdbcAggregateTemplate.insert(bookToCreate);

        boolean existing = bookRepository.existsByIsbn(bookIsbn);

        assertThat(existing).isTrue();
    }

    @Test
    void existsByIsbnWhenNotExisting() {
        boolean existing = bookRepository.existsByIsbn("1234561240");
        assertThat(existing).isFalse();
    }

    @Test
    void deleteByIsbn() {
        var bookIsbn = "1234561241";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 12.90);
        var persistedBook = jdbcAggregateTemplate.insert(bookToCreate);

        bookRepository.deleteByIsbn(bookIsbn);

        assertThat(jdbcAggregateTemplate.findById(persistedBook.id(), Book.class)).isNull();
    }
}
