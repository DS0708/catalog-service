package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

//통합 테스트

//완전한 스프링 웹 애플리케이션 콘텍스트와 임의의 포트를 듣는 서블릿 컨테이너를 로드
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)//기본적으로 스프링 부트 콘텍스트를 캐시로 저장해 @SpringBootTest 어노테이션이 붙거나
// 혹은 이와 동일하게 설정된 모든 테스트 클래스에서 콘텍스트를 재사용하는 편리한 기능이 있음.
class CatalogServiceApplicationTests {

    //Test에서는 필드 기반 의존성 주입 방식이 여전히 허용 -> 자세한 것은 스프링 프레임워크 공식문서 확인
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90);

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()//요청을 전송
                .expectStatus().isCreated()//HTTP 응답이 "201 생성" 상태를 갖는지 확인
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull(); //HTTP 응답의 본문이 널 값이 아닌지 확인
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn()); //생성된 객체가 예상과 동일한지 확인
                });
    }

    @Test
    void whenGetRequestWithIdThenBookReturned() {
        var bookIsbn = "1231231230";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90);

        //Book 넣어놓기
        Book expectedBook = webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        webTestClient
                .get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPutRequestThenBookUpdated() {
        var bookIsbn = "1231231232";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90);
        Book createdBook = webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        var bookToUpdate = Book.of(createdBook.isbn(), createdBook.title(), createdBook.author(), 7.95);
        webTestClient
                .put()
                .uri("/books/" + bookIsbn)
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
//                    assertThat(actualBook.version()).isEqualTo(createdBook.version()+1); //보류
                });
    }

    @Test
    void whenDeleteRequestThenBookDeleted() {
        var bookIsbn = "1231231233";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90);
        webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated();

        webTestClient
                .delete()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage ->
                        assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
                );
    }
}
