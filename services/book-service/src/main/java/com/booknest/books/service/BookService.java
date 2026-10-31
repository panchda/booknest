
package com.booknest.books.service;

import com.booknest.books.entity.Book;
import com.booknest.books.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(String search) {
        if (search == null || search.isBlank()) {
            return bookRepository.findAll();
        }

        return bookRepository
            .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                search,
                search
            );
    }

   
    @Cacheable(value = "books", key = "#id")
    public Optional<Book> getBookById(Long id) {
        System.out.println("Fetching book from database: " + id);
        return bookRepository.findById(id);
    }
}
