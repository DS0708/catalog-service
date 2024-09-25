package com.polarbookshop.catalogservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.Instant;

//도메인 모델은 Immutable Object인 record로 구현
public record Book (

        @Id     //이 필드를 entity에 대한 primary key로 식별
        Long id,

        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        //정규 표현식을 활용해 길이와 모든 요소가 숫자인지를 확인하는 것으로 제한
        String isbn,

        @NotBlank(message = "The book title must be defined.")
        String title,

        @NotBlank(message = "The book author must be defined.")
        String author,

        @NotNull(message = "The book price must be defined.")
        @Positive(message = "The book price must be greater than zero.") //0보다 큰 값을 가져야함
        Double price,

        String publisher, //새로운 선택적 필드, 기존에 존재하는 데이터 중에 publisher가 없는 데이터가 있으므로 값을 반드시 갖지 않아도 되는 선택적 필드로 선언해야함

        @CreatedDate    //entity가 생성된 때
        Instant createdDate,
        //Auditing가 활성화되면 Entity field에 이러한 annotation을 사용해 auditing 메타데이터를 캡처할 수 있다.
        @LastModifiedDate       //entity가 마지막으로 수정된 때
        Instant lastModifiedDate,

        @Version        //Optimistic Locking을 위해 사용되는 entity의 버전 번호
        int version
){
        public static Book of(String isbn, String title, String author, Double price, String publisher) {
                //ID가 null이고 버전이 0이면 새로운 entity로 인식한다.
                return new Book(null, isbn, title, author, price,publisher,null, null, 0);
        }
}
/*
* 스프링 데이터 JPA는 Mutable Ojbect를 사용하기 때문에 Java record를 사용할 수 없다.
* JPA entity Class는 @Entity annotation으로 표시하며 인수를 갖지 않는 기본 생성자를 가져야 한다.
* JPA 식별자는 org.spring.framework.data.annotation 대신 javax.persistence 패키지의 @Id 및 @Version annotation을 사용해야 한다.
* 따라서, 지금은 Spring Data JDBC를 사용하기 때문에 Immutable Object인 record 사용 가능
* */
