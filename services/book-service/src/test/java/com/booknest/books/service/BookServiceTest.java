package com.booknest.books.service;

import com.booknest.books.dto.BookRequest;
import com.booknest.books.entity.Book;
import com.booknest.books.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldReturnAllBooksWhenSearchIsNull() {
        Book firstBook = new Book();
        Book secondBook = new Book();

        when(bookRepository.findAll())
                .thenReturn(List.of(firstBook, secondBook));

        List<Book> result = bookService.getAllBooks(null);

        assertEquals(2, result.size());
        assertSame(firstBook, result.get(0));
        assertSame(secondBook, result.get(1));

        verify(bookRepository).findAll();
        verify(bookRepository, never())
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        anyString(), anyString()
                );
    }

    @Test
    void shouldReturnAllBooksWhenSearchIsBlank() {
        when(bookRepository.findAll())
                .thenReturn(List.of());

        List<Book> result = bookService.getAllBooks("   ");

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    @Test
    void shouldSearchBooksByTitleOrAuthor() {
        String search = "Tolkien";
        Book book = new Book();

        when(bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        search, search
                ))
                .thenReturn(List.of(book));

        List<Book> result = bookService.getAllBooks(search);

        assertEquals(1, result.size());
        assertSame(book, result.get(0));

        verify(bookRepository)
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        search, search
                );
        verify(bookRepository, never()).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoBooksMatchSearch() {
        String search = "Nonexistent";

        when(bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        search, search
                ))
                .thenReturn(List.of());

        List<Book> result = bookService.getAllBooks(search);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindBookById() {
        Long bookId = 1L;
        Book book = new Book();

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(bookId);

        assertTrue(result.isPresent());
        assertSame(book, result.get());

        verify(bookRepository).findById(bookId);
    }

    @Test
    void shouldReturnEmptyOptionalWhenBookDoesNotExist() {
        Long bookId = 99999L;

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(bookId);

        assertTrue(result.isEmpty());

        verify(bookRepository).findById(bookId);
    }

    @Test
    void shouldCreateBook() {
        BookRequest request = new BookRequest(
                " The Little Prince ",
                " Antoine de Saint-Exupéry ",
                "Fiction",
                "A philosophical novella."
        );

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.createBook(request);

        assertEquals("The Little Prince", result.getTitle());
        assertEquals("Antoine de Saint-Exupéry", result.getAuthor());
        assertEquals("Fiction", result.getGenre());
        assertEquals("A philosophical novella.", result.getDescription());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldUpdateExistingBook() {
        Long bookId = 1L;

        Book existingBook = new Book(
                "Old Title",
                "Old Author",
                "Old Genre",
                "Old Description"
        );

        BookRequest request = new BookRequest(
                "Updated Title",
                "Updated Author",
                "Updated Genre",
                "Updated Description"
        );

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(existingBook))
                .thenReturn(existingBook);

        Optional<Book> result = bookService.updateBook(bookId, request);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Author", result.get().getAuthor());
        assertEquals("Updated Genre", result.get().getGenre());
        assertEquals("Updated Description", result.get().getDescription());

        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldNotUpdateNonexistentBook() {
        Long bookId = 99999L;

        BookRequest request = new BookRequest(
                "New Title",
                "New Author",
                "Fiction",
                "Description"
        );

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        Optional<Book> result = bookService.updateBook(bookId, request);

        assertTrue(result.isEmpty());

        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldDeleteExistingBook() {
        Long bookId = 1L;

        when(bookRepository.existsById(bookId))
                .thenReturn(true);

        boolean result = bookService.deleteBook(bookId);

        assertTrue(result);

        verify(bookRepository).existsById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    void shouldNotDeleteNonexistentBook() {
        Long bookId = 99999L;

        when(bookRepository.existsById(bookId))
                .thenReturn(false);

        boolean result = bookService.deleteBook(bookId);

        assertFalse(result);

        verify(bookRepository).existsById(bookId);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldReturnAllBooksWhenSearchIsEmpty() {
        when(bookRepository.findAll())
                .thenReturn(List.of());

        List<Book> result = bookService.getAllBooks("");

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }
}
