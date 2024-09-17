package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// slice test : 애플리케이션 슬라이스에서 요청한 설정만 포함하는 애플리케이션 콘텍스트를 가지고 테스트를 수행
// 따라서 BookService와 같은 슬라이스 외부의 Bean이 필요한 경우에는 해당 클래스에 대한 목 객체를 사용

/*
* @WebMvcTest는 실행 서버 환경이 아닌 모의 웹 환경에서 스프링 애플리케이션 콘텍스트를 로드하고
* 스프링 MVC 인프라를 설정하며 @RestController 및 @RestControllerAdvice와 같은
* MVC 계층에서 사용되는 빈만 포함한다.
* */
@WebMvcTest(BookController.class)
public class BookControllerMvcTests {

    //톰캣과 같은 서버를 로드하지 않고도 웹 엔드포인트를 테스트할 수 있는 유틸리티 class
    //때문에 @SpringBootTest보다 경량이며, 이유는 테스트를 실행하기 위해 임베디드 서버가 필요하지 않기 때문이다.
    @Autowired
    private MockMvc mockMvc;
    //스프링 애플리케이션 콘텍스트에 BookService의 모의 객체를 추가
    @MockBean
    private BookService bookService;
    //@MockBean 어노테이션으로 생성한 모의 객체는 애플리케이션 콘텍스트에 포함된다는 점에서
    //모키토로 생성한 표준 모의 객체와는 다르다.

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";
        //모의 Bean이 어떻게 작동할 것인지 정의
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);
        //MockMvc는 HTTP GET 요청을 수행하고 결과를 확인하기 위해 사용
        mockMvc.perform(get("/books/" + isbn))
                //응답이 404인 상태를 가질 것으로 예상
                .andExpect(status().isNotFound());
    }
}
