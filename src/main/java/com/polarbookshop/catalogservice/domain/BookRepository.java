package com.polarbookshop.catalogservice.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Spring Data가 제공하는 Repository의 구현을 상속
public interface BookRepository extends CrudRepository<Book, Long> { //Entity(Book)와 primary key 유형(Long)을 지정하면서 CURD 연산을 제공하는 Repository를 확장
    Optional<Book> findByIsbn(String isbn); //run-time에 Spring Data에 의해 구현이 제공되는 Method
    boolean existsByIsbn(String isbn);

    @Modifying  //DB 상태를 수정할 연산임을 나타냄
    @Transactional //데이터 베이스의 상태를 변경하므로 트랜잭션으로 처리해야함
    @Query("delete from Book where isbn = :isbn")   //Spring Data가 Method 구현에 사용할 쿼리를 선언
    void deleteByIsbn(String isbn);
}
