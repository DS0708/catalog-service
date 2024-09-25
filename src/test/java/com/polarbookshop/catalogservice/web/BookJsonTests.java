package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

//JSON 슬라이스에 대한 통합 테스트

//도메인 객체에 대한 JSON 직렬화(serialization), 역직렬화(deserialization)를 테스트할 수 있다.
//@JsonTest는 스프링 애플리케이션 콘텍스트를 로드하고, 사용 중인 특정 라이브러리에 대한 JSON 매퍼를 자동으로 구성한다.
@JsonTest
public class BookJsonTests {

    //JSON serialization & deserialization을 확인하기 위한 Utility Class
    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception{
        var now = Instant.now();
        var book = new Book(394L, "1234567890", "Title", "Author", 9.90,"Polarsophia", now, now, 21);
        var jsonContent = json.write(book);

        // JsonPath 형식을 사용해 JSON 객체를 탐색하고 자바의 JSON 변환을 확인
        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(book.id().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());
        assertThat(jsonContent).extractingJsonPathStringValue("@.publisher")
                .isEqualTo(book.publisher());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(book.createdDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(book.lastModifiedDate().toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(book.version());
    }

    @Test
    void testDeserialize() throws Exception{
        var instant = Instant.parse("2021-09-07T22:50:37.135029Z");
        //자바 텍스트 블록 기능을 사용해 JSON 객체를 정의
        var content =  """
                {
                    "id": 394,
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90,
                    "publisher": "Polarsophia",
                    "createdDate": "2021-09-07T22:50:37.135029Z",
                    "lastModifiedDate": "2021-09-07T22:50:37.135029Z",
                    "version": 21
                }
                """;
        assertThat(json.parse(content)) //JSON -> 자바객체 로의 변환
                .usingRecursiveComparison()
                .isEqualTo(new Book(394L, "1234567890", "Title", "Author", 9.90,"Polarsophia", instant, instant, 21));
    }

}
