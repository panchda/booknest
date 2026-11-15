package com.booknest.books.service;

import com.booknest.books.dto.BookRequest;
import com.booknest.books.entity.Book;
import com.booknest.books.repository.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(BookCacheTest.TestConfig.class)
class BookCacheTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        reset(bookRepository);

        cacheManager.getCache("books").clear();
    }

    @Test
    void shouldCacheBookAfterFirstRequest() {
        Long bookId = 1L;

        Book book = new Book(
                "1984",
                "George Orwell",
                "Dystopian",
                "A dystopian novel"
        );

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        Optional<Book> firstResult = bookService.getBookById(bookId);
        Optional<Book> secondResult = bookService.getBookById(bookId);

        assertTrue(firstResult.isPresent());
        assertTrue(secondResult.isPresent());

        assertEquals("1984", secondResult.get().getTitle());

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldEvictCacheAfterUpdatingBook() {
        Long bookId = 1L;

        Book originalBook = new Book(
                "Old Title",
                "Author",
                "Fiction",
                "Description"
        );

        Book updatedBook = new Book(
                "Updated Title",
                "Author",
                "Fiction",
                "Description"
        );

        when(bookRepository.findById(bookId))
                .thenReturn(
                        Optional.of(originalBook),
                        Optional.of(originalBook),
                        Optional.of(updatedBook)
                );

        when(bookRepository.save(any(Book.class)))
                .thenReturn(updatedBook);

        // First GET: populate cache
        bookService.getBookById(bookId);

        BookRequest request = new BookRequest(
                "Updated Title",
                "Author",
                "Fiction",
                "Description"
        );

        // PUT: update book and evict cache
        bookService.updateBook(bookId, request);

        // GET after update: should query repository again
        Optional<Book> result = bookService.getBookById(bookId);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());

        verify(bookRepository, times(3)).findById(bookId);
    }

    @Test
    void shouldEvictCacheAfterDeletingBook() {
        Long bookId = 1L;

        Book book = new Book(
                "1984",
                "George Orwell",
                "Dystopian",
                "Description"
        );

        when(bookRepository.findById(bookId))
                .thenReturn(
                        Optional.of(book),
                        Optional.empty()
                );

        when(bookRepository.existsById(bookId))
                .thenReturn(true);

        // First GET: populate cache
        bookService.getBookById(bookId);

        // DELETE: remove book and evict cache
        boolean deleted = bookService.deleteBook(bookId);

        // GET after delete: should query repository again
        Optional<Book> result = bookService.getBookById(bookId);

        assertTrue(deleted);
        assertTrue(result.isEmpty());

        verify(bookRepository, times(2)).findById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Configuration
    @EnableCaching
    static class TestConfig {

        @Bean
        BookRepository bookRepository() {
            return mock(BookRepository.class);
        }

        @Bean
        BookService bookService(BookRepository bookRepository) {
            return new BookService(bookRepository);
        }

        @Bean
        CacheManager cacheManager() {
            CaffeineCacheManager manager = new CaffeineCacheManager("books");

            manager.setCacheSpecification(
                    "maximumSize=100,expireAfterWrite=10m"
            );

            return manager;
        }
    }
}
