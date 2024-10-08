package com.polarbookshop.catalogservice.domain;

import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    //생성자 오토와이어링, 스프링 4.3부터 생성자가 하나일 경우 @Autowired 어노테이션 생략 가능
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> viewBookList() {
        return bookRepository.findAll();
    }

    public Book viewBookDetails(String isbn) {
        //존재하지 않는 책을 보려고 할 때 그에 해당하는 예외를 발생
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Book addBookToCatalog(Book book) {
        if (bookRepository.existsByIsbn(book.isbn())) {
            //동일한 책을 여러번 추가하려고 할 때 에외 처리
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepository.save(book);
    }

    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Book editBookDetails(String isbn, Book book) {
        return bookRepository.findByIsbn(isbn)
                .map(existingBook -> {
                    var bookToUpdate = new Book(
                            existingBook.id(), //기존 책의 식별자 사용
                            existingBook.isbn(),//책 수정시, ISBN 코드를 제외한 모든 필드 수정 가능
                            book.title(),
                            book.author(),
                            book.price(),
                            book.publisher(),
                            existingBook.createdDate(), //기존 Book record의 생성 날짜 사용
                            existingBook.lastModifiedDate(), //일단 기존 책 레코드의 마지막 수정 날짜를 사용하지만, 업데이트가 성공하면 Spring Data에 의해 자동으로 변경된다.
                            existingBook.version()); //기존 책 버전 사용 시 업데이트가 성공하면 자동으로 증가
                    return bookRepository.save(bookToUpdate);
                })
                .orElseGet(() -> addBookToCatalog(book));
                //카탈로그에 존재하지 않는 책을 수저앟려고 하면 새로운 책을 만든다.
    }

}
