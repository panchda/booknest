package com.booknest.books.service;

import com.booknest.books.dto.BookRequest;
import com.booknest.books.entity.Book;
import com.booknest.books.repository.BookRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
        return bookRepository.findById(id);
    }

    public Book createBook(BookRequest request) {
        Book book = new Book(
            request.title().trim(),
            request.author().trim(),
            request.genre(),
            request.description()
        );

        return bookRepository.save(book);
    }

    @CacheEvict(value = "books", key = "#id")
    public Optional<Book> updateBook(Long id, BookRequest request) {
        return bookRepository.findById(id)
            .map(book -> {
                book.setTitle(request.title().trim());
                book.setAuthor(request.author().trim());
                book.setGenre(request.genre());
                book.setDescription(request.description());

                return bookRepository.save(book);
            });
    }

    @CacheEvict(value = "books", key = "#id")
    public boolean deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }

        bookRepository.deleteById(id);
        return true;
    }
}
